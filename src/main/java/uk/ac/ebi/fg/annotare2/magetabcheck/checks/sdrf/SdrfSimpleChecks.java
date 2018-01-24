/*
 * Copyright 2012 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.MageTabCheckEfo;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.FileLocation;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.HTS_ONLY;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.MICRO_ARRAY_ONLY;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDynamicDetailSetter.setCheckDynamicDetail;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckModality.WARNING;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPositionSetter.setCheckPosition;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf.IdfConstants.DATE_FORMAT;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.matchers.IsDateString.isDateString;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.matchers.IsValidFileLocation.isValidFileLocation;
import static uk.ac.ebi.fg.annotare2.magetabcheck.efo.MageTabCheckEfo.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfSimpleChecks {

    MageTabCheckEfo efo;

    @Inject
    public SdrfSimpleChecks(MageTabCheckEfo efo) {
        this.efo = efo;
    }

    @MageTabCheck(
            ref = "SR01",
            value = "A source (starting sample for the experiment) must have name specified")
    @SuppressWarnings("unused")
    public void sourceNodeMustHaveName(SdrfSourceNode sourceNode) {
        assertNotEmptyName(sourceNode);
    }

    @MageTabCheck(
            ref = "SR02",
            value = "A source (starting sample for the experiment) should have a 'Material Type' attribute specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void sourceNodeShouldHaveMaterialTypeAttribute(SdrfSourceNode sourceNode) {
        setLinePosition(sourceNode);
        assertNotNull(sourceNode.getMaterialType());
        setCellPosition(sourceNode.getMaterialType());
        assertNotEmptyString(sourceNode.getMaterialType().getValue());
    }

    @MageTabCheck(
            ref = "SR03",
            value = "A source (starting sample for the experiment) should have 'Provider' attribute specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void sourceNodeShouldHaveProviderAttribute(SdrfSourceNode sourceNode) {
        setLinePosition(sourceNode);
        assertNotNull(sourceNode.getProvider());
        setCellPosition(sourceNode.getProvider());
        assertNotEmptyString(sourceNode.getProvider().getValue());
    }

    @MageTabCheck(
            ref = "SR04",
            value = "A source (starting sample for the experiment) must have an 'Organism' characteristic specified")
    @SuppressWarnings("unused")
    public void sourceNodeMustHaveOrganismCharacteristic(SdrfSourceNode sourceNode) {
        setLinePosition(sourceNode);
        Collection<SdrfCharacteristicAttribute> characteristics = sourceNode.getCharacteristics();
        assertNotNull(characteristics);
        assertThat(characteristics.isEmpty(), is(Boolean.FALSE));
        SdrfCharacteristicAttribute organism = getOrganism(characteristics);
        assertNotNull(organism);
        setCellPosition(organism);
        assertNotEmptyString(organism.getValue());
    }

    private SdrfCharacteristicAttribute getOrganism(Collection<SdrfCharacteristicAttribute> characteristics) {
        for (SdrfCharacteristicAttribute attr : characteristics) {
            if ("Organism".equalsIgnoreCase(attr.getType())) {
                return attr;
            }
        }
        return null;
    }

    @MageTabCheck(
            ref = "SR05",
            value = "A source (starting sample for the experiment) should have more than 2 characteristic attributes",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void sourceNodeShouldHaveMoreThan2Characteristics(SdrfSourceNode sourceNode) {
        setLinePosition(sourceNode);
        Collection<SdrfCharacteristicAttribute> characteristics = sourceNode.getCharacteristics();
        assertNotNull(characteristics);
        assertThat(characteristics.size(), greaterThanOrEqualTo(2));
    }

    /* not sure what is the idea behind this check
    @MageTabCheck(
            ref = "SR07",
            value = "A source (starting sample for the experiment) should be described by a protocol",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void sourceNodeShouldBeDescribedByProtocol(SdrfSourceNode sourceNode) {
        assertNodeIsDescribedByProtocol(sourceNode);
    }
    */

    @MageTabCheck(
            ref = "SR08",
            value = "A growth, treatment or sample collection protocol must be included")
    @SuppressWarnings("unused")
    public void growthTreatmentOrSampleCollectionProtocolMustBeDefined(SdrfSourceNode sourceNode) {
        setLinePosition(sourceNode);
        assertThat(
                isProtocolTypeMatching(
                        getFollowingProtocolNodes(sourceNode),
                        GROWTH_PROTOCOL,
                        TREATMENT_PROTOCOL,
                        SAMPLE_COLLECTION_PROTOCOL),
                is(Boolean.TRUE));
    }

    private boolean isProtocolTypeMatching(Collection<SdrfProtocolNode> protocolNodes, String... protocolTypes) {
        for (SdrfProtocolNode protocolNode : protocolNodes) {
            if (null == protocolNode.getProtocol()) {
                continue;
            }
            ProtocolType type = protocolNode.getProtocol().getType();
            for (String matchingType : protocolTypes) {
                if (efo.isProtocolType(type, matchingType)) {
                    return true;
                }
            }
        }
        return false;
    }

    @MageTabCheck(
            ref = "SM01",
            value = "A sample must have name specified")
    @SuppressWarnings("unused")
    public void sampleNodeMustHaveName(SdrfSampleNode sampleNode) {
        setCellPosition(sampleNode);
        assertNotEmptyName(sampleNode);
    }

    @MageTabCheck(
            ref = "SM02",
            value = "A sample should have a 'Material Type' attribute specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void sampleNodeShouldHaveMaterialTypeAttribute(SdrfSampleNode sampleNode) {
        setLinePosition(sampleNode);
        assertNotNull(sampleNode.getMaterialType());
    }

    @MageTabCheck(
            ref = "SM03",
            value = "A sample should be described by a protocol",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void sampleNodeShouldBeDescribedByProtocol(SdrfSampleNode sampleNode) {
        setLinePosition(sampleNode);
        assertNodeIsDescribedByProtocol(sampleNode);
    }

    @MageTabCheck(
            ref = "EX01",
            value = "An extract must have name specified")
    @SuppressWarnings("unused")
    public void extractNodeMustHaveName(SdrfExtractNode extractNode) {
        setCellPosition(extractNode);
        assertNotEmptyName(extractNode);
    }

    @MageTabCheck(
            ref = "EX02",
            value = "An extract should have a 'Material Type' attribute specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void extractNodeShouldHaveMaterialTypeAttribute(SdrfExtractNode extractNode) {
        setLinePosition(extractNode);
        assertNotNull(extractNode.getMaterialType());
    }

    @MageTabCheck(
            ref = "EX03",
            value = "A nucleic acid extraction protocol must be included",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void extractNodeShouldBeDescribedByProtocol(SdrfExtractNode extractNode) {
        setLinePosition(extractNode);
        assertThat(
                isProtocolTypeMatching(
                        getParentProtocolNodes(extractNode),
                        EXTRACTION_PROTOCOL),
                is(Boolean.TRUE));
    }

    @MageTabCheck(
            ref = "EX04",
            value = "A nucleic acid library construction protocol must be included",
            application = HTS_ONLY)
    @SuppressWarnings("unused")
    public void extractNodeMustBeDescribedByLibraryConstructionProtocol(SdrfExtractNode extractNode) {
        setLinePosition(extractNode);
        assertThat(
                isProtocolTypeMatching(
                        getParentProtocolNodes(extractNode),
                        LIBRARY_CONSTRUCTION_PROTOCOL),
                is(Boolean.TRUE));
    }

    @MageTabCheck(
            ref = "LE02",
            value = "A labeled extract must have name specified",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void labeledExtractNodeMustHaveName(SdrfLabeledExtractNode labeledExtractNode) {
        setCellPosition(labeledExtractNode);
        assertNotEmptyName(labeledExtractNode);
    }

    @MageTabCheck(
            ref = "LE03",
            value = "A labeled extract should have a 'Material Type' attribute specified",
            modality = WARNING,
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void labeledExtractNodeShouldHaveMaterialTypeAttribute(SdrfLabeledExtractNode labeledExtractNode) {
        setLinePosition(labeledExtractNode);
        assertNotNull(labeledExtractNode.getMaterialType());
    }

    @MageTabCheck(
            ref = "LE04",
            value = "A labeled extract must have 'Label' attribute specified",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void labeledExtractNodeMustHaveLabelAttribute(SdrfLabeledExtractNode labeledExtractNode) {
        setLinePosition(labeledExtractNode);
        assertNotNull(labeledExtractNode.getLabel());
    }

    @MageTabCheck(
            ref = "LE05",
            value = "A nucleic acid labeling protocol must be included",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void labeledExtractNodeShouldBeDescribedByProtocol(SdrfLabeledExtractNode labeledExtractNode) {
        setLinePosition(labeledExtractNode);
        assertThat(
                isProtocolTypeMatching(
                        getParentProtocolNodes(labeledExtractNode),
                        LABELING_PROTOCOL),
                is(Boolean.TRUE));
    }

    @MageTabCheck(
            ref = "L01",
            value = "A label attribute should have name specified",
            modality = WARNING,
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void labelAttributeShouldHaveName(SdrfLabelAttribute labelAttribute) {
        setCellPosition(labelAttribute);
        assertNotEmptyName(labelAttribute);
    }

    @MageTabCheck(
            ref = "L02",
            value = "A label attribute should have term source specified",
            modality = WARNING,
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void labelAttributeShouldHaveTermSource(SdrfLabelAttribute labelAttribute) {
        setCellPosition(labelAttribute);
        assertNotEmptyString(labelAttribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "L03",
            value = "Term source of a label attribute must be defined in IDF",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void termSourceOfLabelAttributeMustBeValid(SdrfLabelAttribute labelAttribute) {
        setCellPosition(labelAttribute);
        assertTermSourceIsValid(labelAttribute);
    }

    @MageTabCheck(
            ref = "MT01",
            value = "A material type attribute should have a name specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void materialTypeAttributeShouldHaveName(SdrfMaterialTypeAttribute materialTypeAttribute) {
        setCellPosition(materialTypeAttribute);
        assertNotEmptyName(materialTypeAttribute);
    }

    @MageTabCheck(
            ref = "MT02",
            value = "A material type attribute should have a term source specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void materialTypeAttributeShouldHaveTermSource(SdrfMaterialTypeAttribute materialTypeAttribute) {
        setCellPosition(materialTypeAttribute);
        assertNotEmptyString(materialTypeAttribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "MT03",
            value = "Term source of a material type attribute must be defined in IDF")
    @SuppressWarnings("unused")
    public void termSourceOfMaterialTypeAttributeMustBeValid(SdrfMaterialTypeAttribute materialTypeAttribute) {
        setCellPosition(materialTypeAttribute);
        assertTermSourceIsValid(materialTypeAttribute);
    }

    @MageTabCheck(
            ref = "PN01",
            value = "A protocol must have a name specified")
    @SuppressWarnings("unused")
    public void protocolNodeMustHaveName(SdrfProtocolNode protocolNode) {
        assertNotEmptyName(protocolNode);
    }

    @MageTabCheck(
            ref = "PN04",
            value = "A protocol node should have a term source specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void protocolNodeShouldHaveTermSource(SdrfProtocolNode protocolNode) {
        setCellPosition(protocolNode);
        assertNotEmptyString(protocolNode.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "PN05",
            value = "Term source value of a protocol node must be defined in IDF")
    @SuppressWarnings("unused")
    public void termSourceOfProtocolMustBeValid(SdrfProtocolNode protocolNode) {
        setCellPosition(protocolNode);
        assertTermSourceIsValid(protocolNode);
    }

    @MageTabCheck(
            ref = "PN03",
            value = "A protocol's date must be in 'YYYY-MM-DD' format")
    @SuppressWarnings("unused")
    public void protocolNodeDateFormat(SdrfProtocolNode protocolNode) {
        String date = protocolNode.getDate();
        if (isNullOrEmpty(date)) {
            return;
        }
        setCellPosition(protocolNode);
        assertThat(date, isDateString(DATE_FORMAT));
    }

    @MageTabCheck(
            ref = "PN06",
            value = "A nucleic acid sequencing protocol must have a 'performer' attribute specified",
            application = HTS_ONLY)
    @SuppressWarnings("unused")
    public void sequencingProtocolNodeMustHavePerformerAttribute(SdrfProtocolNode protocolNode) {
        Protocol protocol = protocolNode.getProtocol();
        if (protocol != null && efo.isProtocolType(protocol.getType(), SEQUENCING_PROTOCOL)) {
            assertProtocolHasPerformerAttribute(protocolNode);
        }
    }

    @MageTabCheck(
            ref = "PN07",
            value = "A protocol should have a 'performer' attribute specified",
            modality = WARNING,
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void protocolNodeShouldHavePerformerAttribute(SdrfProtocolNode protocolNode) {
        assertProtocolHasPerformerAttribute(protocolNode);
    }

    private void assertProtocolHasPerformerAttribute(SdrfProtocolNode protocolNode) {
        setLinePosition(protocolNode);
        SdrfPerformerAttribute attr = protocolNode.getPerformer();
        assertNotNull(attr);

        setCellPosition(attr);
        assertNotEmptyString(attr.getValue());
    }

    @MageTabCheck(
            ref = "AN01",
            value = "An assay must have a name specified")
    @SuppressWarnings("unused")
    public void assayNodeMustHaveName(SdrfAssayNode assayNode) {
        assertNotEmptyName(assayNode);
    }

    @MageTabCheck(
            ref = "AN02",
            value = "An assay must have a 'Technology Type' attribute specified")
    @SuppressWarnings("unused")
    public void assayNodeMustHaveTechnologyTypeAttribute(SdrfAssayNode assayNode) {
        setCellPosition(assayNode);
        assertNotNull(assayNode.getTechnologyType());
    }

    @MageTabCheck(
            ref = "AN03",
            value = "A nucleic acid sequencing protocol must be included",
            application = HTS_ONLY)
    @SuppressWarnings("unused")
    public void assayNodeMustBeDescribedBySequencingProtocol(SdrfAssayNode assayNode) {
        setLinePosition(assayNode);
        assertThat(
                isProtocolTypeMatching(
                        getParentProtocolNodes(assayNode),
                        SEQUENCING_PROTOCOL),
                is(Boolean.TRUE));
    }

    @MageTabCheck(
            ref = "AN04",
            value = "A nucleic acid hybridization to array protocol must be included",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void assayNodeMustBeDescribedByHybridizationProtocol(SdrfAssayNode assayNode) {
        setLinePosition(assayNode);
        assertThat(
                isProtocolTypeMatching(
                        getParentProtocolNodes(assayNode),
                        ARRAY_HYBRIDIZATION_PROTOCOL),
                is(Boolean.TRUE));
    }

    @MageTabCheck(
            ref = "AN05",
            value = "'Technology Type' attribute must be equal to 'array assay' in micro-array submissions",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void assayNodeTechnologyTypeIsArrayAssay(SdrfAssayNode assayNode) {
        setLinePosition(assayNode);
        assertNotNull(assayNode.getTechnologyType());
        setCellPosition(assayNode.getTechnologyType());
        assertThat(assayNode.getTechnologyType().getValue().trim(), equalToIgnoringCase("array assay"));
    }

    @MageTabCheck(
            ref = "AN06",
            value = "For an array assay (microarray experiment) the incoming nodes must be 'Labeled Extract' nodes only",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void assayNodeMustBeDerivedFromLabeledExtracts(SdrfAssayNode assayNode) {
        setLinePosition(assayNode);
        Collection<SdrfGraphNode> parents = getParentNodes(assayNode);
        Collection<SdrfGraphNode> filtered = filter(parents, new Predicate<SdrfGraphNode>() {
            @Override
            public boolean apply(@Nullable SdrfGraphNode input) {
                return null != input && input instanceof SdrfLabeledExtractNode;
            }
        });
        assertThat(parents.size(), equalTo(filtered.size()));
    }

    @MageTabCheck(
            ref = "TT01",
            value = "Technology type attribute must have name specified")
    @SuppressWarnings("unused")
    public void technologyTypeMustHaveName(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        assertNotEmptyName(technologyTypeAttribute);
    }

    @MageTabCheck(
            ref = "TT02",
            value = "Technology type attribute should have term source specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void technologyTypeShouldHaveTermSource(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        setCellPosition(technologyTypeAttribute);
        assertNotEmptyString(technologyTypeAttribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "TT03",
            value = "Term source of a technology type attribute must be defined in IDF")
    @SuppressWarnings("unused")
    public void termSourceOfTechnologyTypeMustBeValied(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        setCellPosition(technologyTypeAttribute);
        assertTermSourceIsValid(technologyTypeAttribute);
    }

    @MageTabCheck(
            ref = "PV01",
            value = "A parameter value attribute (of a protocol) should have a name specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void parameterValueAttributeShouldHaveName(SdrfParameterValueAttribute parameterValueAttribute) {
        setCellPosition(parameterValueAttribute);
        assertNotEmptyString(parameterValueAttribute.getType());
    }

    @MageTabCheck(
            ref = "PV02",
            value = "A parameter value attribute (of a protocol) should have a unit specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void parameterValueAttributeShouldHaveUnit(SdrfParameterValueAttribute parameterValueAttribute) {
        setCellPosition(parameterValueAttribute);
        assertNotNull(parameterValueAttribute.getUnit());
    }

    @MageTabCheck(
            ref = "UA01",
            value = "A unit attribute should have name specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void unitAttributeShouldHaveName(SdrfUnitAttribute unitAttribute) {
        setCellPosition(unitAttribute);
        assertNotEmptyString(unitAttribute.getType());
    }

    @MageTabCheck(
            ref = "UA02",
            value = "A unit attribute should have term source specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void unitAttributeShouldHaveTermSource(SdrfUnitAttribute unitAttribute) {
        setCellPosition(unitAttribute);
        assertNotEmptyString(unitAttribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "UA03",
            value = "Term source of a unit attribute must be declared in IDF")
    @SuppressWarnings("unused")
    public void termSourceOfUnitAttributeMustBeValid(SdrfUnitAttribute unitAttribute) {
        setCellPosition(unitAttribute);
        assertTermSourceIsValid(unitAttribute);
    }

    @MageTabCheck(
            ref = "LC01",
            value = "Library source, layout, selection and strategy must be specified for the ENA library info",
            application = HTS_ONLY)
    @SuppressWarnings("unused")
    public void extractNodeMustHaveFourLibraryComments(SdrfExtractNode extractNode) {
        setLinePosition(extractNode);
        Collection<String> requiredComments = ImmutableSet.of(
                "LIBRARY_LAYOUT", "LIBRARY_SELECTION", "LIBRARY_SOURCE", "LIBRARY_STRATEGY"
        );
        int count = 0;
        for (SdrfComment comment : extractNode.getComments()) {
            if (requiredComments.contains(comment.getName())) {
                count++;
            }
        }
        assertThat(count, is(requiredComments.size()));
    }

    @MageTabCheck(
            ref = "LC02",
            value = "NOMINAL_LENGTH must be a positive integer and NOMINAL_SDEV must be a positive number for paired-end sequencing samples in the ENA library info",
            application = HTS_ONLY)
    @SuppressWarnings("unused")
    public void extractNodeMustHaveNominalLengthAndSDevSpecifiedForPairedExtracts(SdrfExtractNode extractNode) {
        setLinePosition(extractNode);

        boolean isPaired = false;
        int count = 0;

        for (SdrfComment comment : extractNode.getComments()) {
            if ("LIBRARY_LAYOUT".equals(comment.getName()) && comment.getValues().contains("PAIRED")) {
                isPaired = true;
            }
            if ("NOMINAL_LENGTH".contains(comment.getName()) && isValidPositiveInteger(comment.getValues())) {
                count++;
            }
            if ("NOMINAL_SDEV".contains(comment.getName()) && isValidPositiveDouble(comment.getValues())) {
                count++;
            }
        }

        if (isPaired) {
            assertThat(count, is(2));
        }
    }

    @MageTabCheck(
            ref = "CA01",
            value = "A characteristic attribute should have name specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void characteristicAttributeShouldHaveName(SdrfCharacteristicAttribute attribute) {
        setCellPosition(attribute);
        assertNotEmptyString(attribute.getType());
    }

    @MageTabCheck(
            ref = "CA02",
            value = "A characteristic attribute should have a term source specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void characteristicAttributeShouldHaveTermSource(SdrfCharacteristicAttribute attribute) {
        setCellPosition(attribute);
        assertNotEmptyString(attribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "CA03",
            value = "Term source of a characteristic attribute must be declared in IDF")
    @SuppressWarnings("unused")
    public void termSourceOfCharacteristicAttributeMustBeValid(SdrfCharacteristicAttribute attribute) {
        setCellPosition(attribute);
        assertTermSourceIsValid(attribute);
    }

    @MageTabCheck(
            ref = "FV01",
            value = "An experimental variable attribute should have a name specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void factorValueAttributeShouldHaveName(SdrfFactorValueAttribute fvAttribute) {
        setCellPosition(fvAttribute);
        assertNotEmptyString(fvAttribute.getType());
    }

    @MageTabCheck(
            ref = "FV02",
            value = "An experimental variable attribute should have a term source specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void factorValueAttributeShouldHaveTermSource(SdrfFactorValueAttribute fvAttribute) {
        setCellPosition(fvAttribute);
        assertNotEmptyString(fvAttribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "FV03",
            value = "Term source of an experimental variable attribute must be declared in IDF")
    @SuppressWarnings("unused")
    public void termSourceOfFactorValueAttributeMustBeValid(SdrfFactorValueAttribute fvAttribute) {
        setCellPosition(fvAttribute);
        assertTermSourceIsValid(fvAttribute);
    }

    @MageTabCheck(
            ref = "AD01",
            value = "An array design attribute must have a name specified",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void arrayDesignAttributeMustHaveName(SdrfArrayDesignAttribute adAttribute) {
        assertNotEmptyName(adAttribute);
    }

    @MageTabCheck(
            ref = "AD02",
            value = "An array design should have a term source specified",
            modality = WARNING,
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void arrayDesignAttributeShouldHaveTermSource(SdrfArrayDesignAttribute adAttribute) {
        setCellPosition(adAttribute);
        assertNotEmptyString(adAttribute.getTermSourceRef());
    }

    @MageTabCheck(
            ref = "AD03",
            value = "Term source of an array design attribute must be declared in IDF",
            application = MICRO_ARRAY_ONLY)
    @SuppressWarnings("unused")
    public void termSourceOfArrayDesignAttributeMustBeValid(SdrfArrayDesignAttribute adAttribute) {
        setCellPosition(adAttribute);
        assertTermSourceIsValid(adAttribute);
    }

    @MageTabCheck(
            ref = "NN01",
            value = "A normalization node should have a name",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void normalizationNodeShouldHaveName(SdrfNormalizationNode normalizationNode) {
        assertNotEmptyName(normalizationNode);
    }

    @MageTabCheck(
            ref = "SC01",
            value = "A scan should have a name specified",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void scanNodeShouldHaveName(SdrfScanNode scanNode) {
        assertNotEmptyName(scanNode);
    }

    @MageTabCheck(
            ref = "ADN01",
            value = "An array data node (raw data file) must have a name")
    @SuppressWarnings("unused")
    public void arrayDataNodeMustHaveName(SdrfArrayDataNode arrayDataNode) {
        assertNotEmptyName(arrayDataNode);
    }

    @MageTabCheck(
            ref = "ADN02",
            value = "A raw data file name must only contain alphanumeric characters, underscores and dots")
    @SuppressWarnings("unused")
    public void arrayDataNodeMustHaveFormattedName(SdrfArrayDataNode arrayDataNode) {
        setCellPosition(arrayDataNode);
        assertThat(checkFileName(arrayDataNode), is(true));
    }

    @MageTabCheck(
            ref = "ADN03",
            value = "Name of an array data node must be a valid file location")
    @SuppressWarnings("unused")
    public void nameOfArrayDataNodeMustBeValidFileLocation(SdrfArrayDataNode arrayDataNode) {
        assertFileLocationIsValid(arrayDataNode);
    }

    @MageTabCheck(
            ref = "ADN04",
            value = "An array data node (raw data file) should be described by a protocol",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void arrayDataNodeShouldBeDescribedByProtocol(SdrfArrayDataNode arrayDataNode) {
        setLinePosition(arrayDataNode);
        assertNodeIsDescribedByProtocol(arrayDataNode);
    }

    @MageTabCheck(
            ref = "DADN01",
            value = "A derived array data node (processed data file) must have name specified")
    @SuppressWarnings("unused")
    public void derivedArrayDataNodeMustHaveName(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        assertNotEmptyName(derivedArrayDataNode);
    }


    @MageTabCheck(
            ref = "DADN02",
            value = "A processed data file name must only contain alphanumeric characters, underscores and dots")
    @SuppressWarnings("unused")
    public void derivedArrayDataNodeMustHaveFormattedName(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        setCheckDynamicDetail("offending factor: " + derivedArrayDataNode.getName());
        setCellPosition(derivedArrayDataNode);
        assertThat(checkProcessedFileName(derivedArrayDataNode), is(true));
    }

    @MageTabCheck(
            ref = "DADN03",
            value = "Name of a derived array data node must be a valid file location")
    @SuppressWarnings("unused")
    public void nameOfDerivedArrayDataNodeMustBeValidFileLocation(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        assertFileLocationIsValid(derivedArrayDataNode);
    }

    @MageTabCheck(
            ref = "DADN04",
            value = "A normalization data transformation protocol that describes the analysis methods used to generate the processed data file(s) must be included")
    @SuppressWarnings("unused")
    public void derivedArrayDataNodeShouldBeDescribedByProtocol(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        setLinePosition(derivedArrayDataNode);
            assertThat(
                    isProtocolTypeMatching(
                            getParentProtocolNodes(derivedArrayDataNode),
                            DATA_TRANSOFRMATION_PROTOCOL),
                    is(Boolean.TRUE));
    }

    @MageTabCheck(
            ref = "ADMN01",
            value = "An array data matrix file must have name specified")
    @SuppressWarnings("unused")
    public void arrayDataMatrixNodeMustHaveName(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        assertNotEmptyName(arrayDataMatrixNode);
    }

    @MageTabCheck(
            ref = "ADMN02",
            value = "An array data matrix file name must only contain alphanumeric characters, underscores and dots")
    @SuppressWarnings("unused")
    public void arrayDataMatrixNodeMustHaveFormattedName(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        setCellPosition(arrayDataMatrixNode);
        assertThat(checkFileName(arrayDataMatrixNode), is(true));
    }

    @MageTabCheck(
            ref = "ADMN03",
            value = "Name of an array data matrix node must be valid file location")
    @SuppressWarnings("unused")
    public void nameOfArrayDataMatrixNodeMustBeValidFileLocation(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        assertFileLocationIsValid(arrayDataMatrixNode);
    }

    @MageTabCheck(
            ref = "ADMN04",
            value = "An array data matrix file should be described by a protocol",
            modality = WARNING)
    @SuppressWarnings("unused")
    public void arrayDataMatrixNodeShouldBeDescribedByProtocol(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        assertNodeIsDescribedByProtocol(arrayDataMatrixNode);
    }

    @MageTabCheck(
            ref = "DADMN01",
            value = "A derived array data matrix file must have a name specified")
    @SuppressWarnings("unused")
    public void derivedArrayDataMatrixNodeMustHaveName(SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        assertNotEmptyName(derivedArrayDataMatrixNode);
    }

    @MageTabCheck(
            ref = "DADMN02",
            value = "A derived array data matrix file name must only contain alphanumeric characters, underscores and dots")
    @SuppressWarnings("unused")
    public void derivedArrayDataMatrixNodeMustHaveFormattedName(SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        setCellPosition(derivedArrayDataMatrixNode);
        assertThat(checkFileName(derivedArrayDataMatrixNode), is(true));
    }

    @MageTabCheck(
            ref = "DADMN03",
            value = "Name of derived data matrix node must be valid file location")
    @SuppressWarnings("unused")
    public void nameOfDerivedArrayDataMatrixNodeMustBeValidFileLocation(
            SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        assertFileLocationIsValid(derivedArrayDataMatrixNode);
    }

    @MageTabCheck(
            ref = "DADMN04",
            value = "A normalization data transformation protocol that describes the analysis methods used to generate the processed data matrix file must be included")
    @SuppressWarnings("unused")
    public void derivedArrayDataMatrixNodeShouldBeDescribedByProtocol(
            SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        setLinePosition(derivedArrayDataMatrixNode);
        assertThat(
                isProtocolTypeMatching(
                        getParentProtocolNodes(derivedArrayDataMatrixNode),
                        DATA_TRANSOFRMATION_PROTOCOL),
                is(Boolean.TRUE));
    }

    private static boolean checkFileName(SdrfDataNode dataNode){
        // We only want to accept files with alphanumeric characters, no spaces, symbols etc.
        String filename = dataNode.getName();
        return null != filename && filename.matches("^[_a-zA-Z0-9\\-\\.]+$");

    }

    private static boolean checkProcessedFileName(SdrfDataNode dataNode){
        // We only want to accept files with alphanumeric characters, no spaces, symbols etc.
        String filename = dataNode.getName();
        return filename.matches("^[_a-zA-Z0-9\\-\\.]+$") || isNullOrEmpty(filename);

    }

    private static boolean isValidPositiveInteger(Collection<String> values) {
        for (String value : values) {
            try {
                if (null == value || Integer.valueOf(value) <= 0) {
                    return false;
                }
            } catch (NumberFormatException x) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidPositiveDouble(Collection<String> values) {
        for (String value : values) {
            try {
                if (null == value || Double.valueOf(value) < 0) {
                    return false;
                }
            } catch (NumberFormatException x) {
                return false;
            }
        }
        return true;
    }

    private static <T> void assertNotNull(T obj) {
        assertThat(obj, notNullValue());
    }

    private static void assertNotEmptyString(String str) {
        assertThat(str, notNullValue());
        assertThat(str.trim(), not(isEmptyString()));
    }

    private static void assertTermSourceIsValid(HasTermSource t) {
        if (isNullOrEmpty(t.getTermSourceRef())) {
            return;
        }
        assertNotNull(t.getTermSource());
    }

    private static void assertNotEmptyName(SdrfGraphEntity node) {
        setCellPosition(node);
        String name = node.getName();
        assertNotEmptyString(name);
    }

    private static void assertNodeIsDescribedByProtocol(SdrfGraphNode node) {
        setLinePosition(node);
        assertThat(getParentProtocolNodes(node), is(not(empty())));
    }

    private static void assertFileLocationIsValid(SdrfDataNode dataNode) {
        FileLocation location = dataNode.getLocation();
        if (!location.isEmpty()) {
            setCellPosition(dataNode);
            assertThat(location, isValidFileLocation());
        }
    }

    private static Collection<SdrfProtocolNode> getFollowingProtocolNodes(SdrfGraphNode node) {
        List<SdrfProtocolNode> protocolNodes = newArrayList();
        if (node instanceof SdrfProtocolNode) {
            protocolNodes.add((SdrfProtocolNode)node);
        }
        for (SdrfGraphNode child : node.getChildNodes()) {
            protocolNodes.addAll(getFollowingProtocolNodes(child));
        }
        return protocolNodes;
    }

    private static Collection<SdrfProtocolNode> getParentProtocolNodes(SdrfGraphNode node) {
        List<SdrfProtocolNode> protocols = newArrayList();
        Queue<SdrfGraphNode> queue = new ArrayDeque<SdrfGraphNode>();
        queue.addAll(node.getParentNodes());
        while (!queue.isEmpty()) {
            SdrfGraphNode parent = queue.poll();
            if (parent instanceof SdrfProtocolNode) {
                protocols.add((SdrfProtocolNode) parent);
                queue.addAll(parent.getParentNodes());
            }
        }
        return protocols;
    }


    private static Collection<SdrfGraphNode> getParentNodes(SdrfGraphNode node) {
        List<SdrfGraphNode> parents = newArrayList();
        for (SdrfGraphNode parent : node.getParentNodes()) {
            if (parent instanceof SdrfProtocolNode) {
                parents.addAll(getParentNodes(parent));
            } else {
                parents.add(parent);
            }
        }
        return parents;
    }

    private static <T extends HasLocation> void setLinePosition(T t) {
        setCheckPosition(t.getFileName(), t.getLine(), -1);
    }

    private static <T extends HasLocation> void setCellPosition(T t) {
        setCheckPosition(t.getFileName(), t.getLine(), t.getColumn());
    }
}

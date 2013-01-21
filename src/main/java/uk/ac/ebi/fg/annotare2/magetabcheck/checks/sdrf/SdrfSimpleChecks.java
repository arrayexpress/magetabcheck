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

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.FileLocation;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;

import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.MICRO_ARRAY_ONLY;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckModality.WARNING;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPositionKeeper.setCheckPosition;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf.IdfConstants.DATE_FORMAT;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.matchers.IsDateString.isDateString;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.matchers.IsValidFileLocation.isValidFileLocation;
import static uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownTermSource.NCBI_TAXONOMY;

/**
 * @author Olga Melnichuk
 */
public class SdrfSimpleChecks {

    @MageTabCheck("A source node must have name specified")
    public void sourceNodeMustHaveName(SdrfSourceNode sourceNode) {
        assertNotEmptyName(sourceNode);
    }

    @MageTabCheck(value = "A source node should have 'Material Type' attribute specified", modality = WARNING)
    public void sourceNodeShouldHaveMaterialTypeAttribute(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        assertNotNull(sourceNode.getMaterialType());
    }

    @MageTabCheck(value = "A source node should have 'Provider' attribute specified", modality = WARNING)
    public void sourceNodeShouldHaveProviderAttribute(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        assertNotNull(sourceNode.getProvider());
        assertNotEmptyString(sourceNode.getProvider().getValue());
    }

    @MageTabCheck("A source node must have an 'Organism' characteristic specified")
    public void sourceNodeMustHaveOrganismCharacteristic(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        Collection<SdrfCharacteristicAttribute> characteristics = sourceNode.getCharacteristics();
        assertNotNull(characteristics);
        assertThat(characteristics.isEmpty(), is(Boolean.FALSE));
        assertNotNull(getOrganism(characteristics));
    }

    private SdrfCharacteristicAttribute getOrganism(Collection<SdrfCharacteristicAttribute> characteristics) {
        for (SdrfCharacteristicAttribute attr : characteristics) {
            if ("Organism".equalsIgnoreCase(attr.getType())) {
                TermSource ts = attr.getTermSource();
                if (ts == null || NCBI_TAXONOMY.matches(ts.getFile().getValue())) {
                    return attr;
                }
            }
        }
        return null;
    }

    @MageTabCheck(value = "A source node should have more than 2 characteristic attributes", modality = WARNING)
    public void sourceNodeShouldHaveMoreThan2Characteristics(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        Collection<SdrfCharacteristicAttribute> characteristics = sourceNode.getCharacteristics();
        assertNotNull(characteristics);
        assertThat(characteristics.size(), greaterThanOrEqualTo(2));
    }

    @MageTabCheck(value = "A source node should be described by a protocol", modality = WARNING)
    public void sourceNodeShouldBeDescribedByProtocol(SdrfSourceNode sourceNode) {
        assertNodeIsDescribedByProtocol(sourceNode);
    }

    @MageTabCheck("A sample node must have name specified")
    public void sampleNodeMustHaveName(SdrfSampleNode sampleNode) {
        assertNotEmptyName(sampleNode);
    }

    @MageTabCheck(value = "A sample node should have 'Material Type' attribute specified", modality = WARNING)
    public void sampleNodeShouldHaveMaterialTypeAttribute(SdrfSampleNode sampleNode) {
        setPosition(sampleNode);
        assertNotNull(sampleNode.getMaterialType());
    }

    @MageTabCheck(value = "A sample node should be described by a protocol", modality = WARNING)
    public void sampleNodeShouldBeDescribedByProtocol(SdrfSampleNode sampleNode) {
        assertNodeIsDescribedByProtocol(sampleNode);
    }

    @MageTabCheck("An extract node must have name specified")
    public void extractNodeMustHaveName(SdrfExtractNode extractNode) {
        assertNotEmptyName(extractNode);
    }

    @MageTabCheck(value = "An extract node should have 'Material Type' attribute specified", modality = WARNING)
    public void extractNodeShouldHaveMaterialTypeAttribute(SdrfExtractNode extractNode) {
        setPosition(extractNode);
        assertNotNull(extractNode.getMaterialType());
    }

    @MageTabCheck(value = "An extract node should be described by a protocol", modality = WARNING)
    public void extractNodeShouldBeDescribedByProtocol(SdrfExtractNode extractNode) {
        assertNodeIsDescribedByProtocol(extractNode);
    }

    @MageTabCheck(value = "A labeled extract node must have name specified", application = MICRO_ARRAY_ONLY)
    public void labeledExtractNodeMustHaveName(SdrfLabeledExtractNode labeledExtractNode) {
        assertNotEmptyName(labeledExtractNode);
    }

    @MageTabCheck(value = "A labeled extract node should have 'Material Type' attribute specified", modality = WARNING, application = MICRO_ARRAY_ONLY)
    public void labeledExtractNodeShouldHaveMaterialTypeAttribute(SdrfLabeledExtractNode labeledExtractNode) {
        setPosition(labeledExtractNode);
        assertNotNull(labeledExtractNode.getMaterialType());
    }

    @MageTabCheck(value = "A labeled extract node must have 'Label' attribute specified", application = MICRO_ARRAY_ONLY)
    public void labeledExtractNodeMustHaveLabelAttribute(SdrfLabeledExtractNode labeledExtractNode) {
        setPosition(labeledExtractNode);
        assertNotNull(labeledExtractNode.getLabel());
    }

    @MageTabCheck(value = "A labeled extract node should be described by a protocol", modality = WARNING, application = MICRO_ARRAY_ONLY)
    public void labeledExtractNodeShouldBeDescribedByProtocol(SdrfLabeledExtractNode labeledExtractNode) {
        assertNodeIsDescribedByProtocol(labeledExtractNode);
    }

    @MageTabCheck(value = "A label attribute should have name specified", modality = WARNING, application = MICRO_ARRAY_ONLY)
    public void labelAttributeShouldHaveName(SdrfLabelAttribute labelAttribute) {
        assertNotEmptyName(labelAttribute);
    }

    @MageTabCheck(value = "A label attribute should have term source specified", modality = WARNING, application = MICRO_ARRAY_ONLY)
    public void labelAttributeShouldHaveTermSource(SdrfLabelAttribute la) {
        setPosition(la);
        assertNotEmptyString(la.getTermSourceRef());
    }

    @MageTabCheck(value = "Term source of a label attribute must be defined in IDF", application = MICRO_ARRAY_ONLY)
    public void termSourceOfLabelAttributeMustBeValid(SdrfLabelAttribute la) {
        setPosition(la);
        assertTermSourceIsValid(la);
    }

    @MageTabCheck(value = "A material type attribute should have name specified", modality = WARNING)
    public void materialTypeAttributeShouldHaveName(SdrfMaterialTypeAttribute materialTypeAttribute) {
        assertNotEmptyName(materialTypeAttribute);
    }

    @MageTabCheck(value = "A material type attribute should have term source specified", modality = WARNING)
    public void materialTypeAttributeShouldHaveTermSource(SdrfMaterialTypeAttribute mta) {
        setPosition(mta);
        assertNotEmptyString(mta.getTermSourceRef());
    }

    @MageTabCheck("Term source of a material type attribute must be defined in IDF")
    public void termSourceOfMaterialTypeAttributeMustBeValid(SdrfMaterialTypeAttribute mta) {
        setPosition(mta);
        assertTermSourceIsValid(mta);
    }

    @MageTabCheck("A protocol node must have name specified")
    public void protocolNodeMustHaveName(SdrfProtocolNode protocolNode) {
        assertNotEmptyName(protocolNode);
    }

    @MageTabCheck(value = "A protocol node should have date specified", modality = WARNING)
    public void protocolNodeShouldHaveDate(SdrfProtocolNode protocolNode) {
        setPosition(protocolNode);
        assertNotEmptyString(protocolNode.getDate());
    }

    @MageTabCheck(value = "A protocol node should have term source specified", modality = WARNING)
    public void protocolNodeShouldHaveTermSource(SdrfProtocolNode protocolNode) {
        setPosition(protocolNode);
        assertNotEmptyString(protocolNode.getTermSourceRef());
    }

    @MageTabCheck("Term source value of a protocol node must be defined in IDF")
    public void termSourceOfProtocolMustBeValid(SdrfProtocolNode protocolNode) {
        setPosition(protocolNode);
        assertTermSourceIsValid(protocolNode);
    }

    @MageTabCheck("A protocol's date must be in 'YYYY-MM-DD' format")
    public void protocolNodeDateFormat(SdrfProtocolNode protocolNode) {
        String date = protocolNode.getDate();
        if (isNullOrEmpty(date)) {
            return;
        }
        setPosition(protocolNode);
        assertThat(date, isDateString(DATE_FORMAT));
    }

    @MageTabCheck("An assay node must have name specified")
    public void assayNodeMustHaveName(SdrfAssayNode assayNode) {
        assertNotEmptyName(assayNode);
    }

    @MageTabCheck("An assay node must have 'Technology Type' attribute specified")
    public void assayNodeMustHaveTechnologyTypeAttribute(SdrfAssayNode assayNode) {
        setPosition(assayNode);
        assertNotNull(assayNode.getTechnologyType());
    }

    @MageTabCheck("Technology type attribute must have name specified")
    public void technologyTypeMustHaveName(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        assertNotEmptyName(technologyTypeAttribute);
    }

    @MageTabCheck(value = "Technology type attribute should have term source specified", modality = WARNING)
    public void technologyTypeShouldHaveTermSource(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        setPosition(technologyTypeAttribute);
        assertNotEmptyString(technologyTypeAttribute.getTermSourceRef());
    }

    @MageTabCheck("Term source of a technology type attribute must be defined in IDF")
    public void termSourceOfTechnologyTypeMustBeValied(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        setPosition(technologyTypeAttribute);
        assertTermSourceIsValid(technologyTypeAttribute);
    }

    @MageTabCheck(value = "A parameter value attribute (of a protocol) should have name specified", modality = WARNING)
    public void parameterValueAttributeShouldHaveName(SdrfParameterValueAttribute parameterValueAttribute) {
        setPosition(parameterValueAttribute);
        assertNotEmptyString(parameterValueAttribute.getType());
    }

    @MageTabCheck(value = "A parameter value attribute (of a protocol) should have unit specified", modality = WARNING)
    public void parameterValueAttributeShouldHaveUnit(SdrfParameterValueAttribute parameterValueAttribute) {
        setPosition(parameterValueAttribute);
        assertNotNull(parameterValueAttribute.getUnit());
    }

    @MageTabCheck(value = "A unit attribute should have name specified", modality = WARNING)
    public void unitAttributeShouldHaveName(SdrfUnitAttribute unitAttribute) {
        setPosition(unitAttribute);
        assertNotEmptyString(unitAttribute.getType());
    }

    @MageTabCheck(value = "A unit attribute should have term source specified", modality = WARNING)
    public void unitAttributeShouldHaveTermSource(SdrfUnitAttribute unitAttribute) {
        setPosition(unitAttribute);
        assertNotEmptyString(unitAttribute.getTermSourceRef());
    }

    @MageTabCheck("Term source of a unit attribute must be declared in IDF")
    public void termSourceOfUnitAttributeMustBeValid(SdrfUnitAttribute unitAttribute) {
        setPosition(unitAttribute);
        assertTermSourceIsValid(unitAttribute);
    }

    @MageTabCheck(value = "A characteristic attribute should have name specified", modality = WARNING)
    public void characteristicAttributeShouldHaveName(SdrfCharacteristicAttribute attribute) {
        setPosition(attribute);
        assertNotEmptyString(attribute.getType());
    }

    @MageTabCheck(value = "A characteristic attribute should have term source specified", modality = WARNING)
    public void characteristicAttributeShouldHaveTermSource(SdrfCharacteristicAttribute attribute) {
        setPosition(attribute);
        assertNotEmptyString(attribute.getTermSourceRef());
    }

    @MageTabCheck("Term source of a characteristic attribute must be declared in IDF")
    public void termSourceOfCharacteristicAttributeMustBeValid(SdrfCharacteristicAttribute attribute) {
        setPosition(attribute);
        assertTermSourceIsValid(attribute);
    }

    @MageTabCheck(value = "A factor value attribute should have name specified", modality = WARNING)
    public void factorValueAttributeShouldHaveName(SdrfFactorValueAttribute fvAttribute) {
        setPosition(fvAttribute);
        assertNotEmptyString(fvAttribute.getType());
    }

    @MageTabCheck(value = "A factor value attribute should have term source specified", modality = WARNING)
    public void factorValueAttributeShouldHaveTermSource(SdrfFactorValueAttribute fvAttribute) {
        setPosition(fvAttribute);
        assertNotEmptyString(fvAttribute.getTermSourceRef());
    }

    @MageTabCheck("Term source of a factor value attribute must be declared in IDF")
    public void termSourceOfFactorValueAttributeMustBeValid(SdrfFactorValueAttribute fvAttribute) {
        setPosition(fvAttribute);
        assertTermSourceIsValid(fvAttribute);
    }

    @MageTabCheck("An array design attribute must have name specified")
    public void arrayDesignAttributeMustHaveName(SdrfArrayDesignAttribute adAttribute) {
        assertNotEmptyName(adAttribute);
    }

    @MageTabCheck(value = "An array design should have term source specified", modality = WARNING)
    public void arrayDesignAttributeShouldHaveTermSource(SdrfArrayDesignAttribute adAttribute) {
        setPosition(adAttribute);
        assertNotEmptyString(adAttribute.getTermSourceRef());
    }

    @MageTabCheck("Term source of an array design attribute must be declared in IDF")
    public void termSourceOfArrayDesignAttributeMustBeValid(SdrfArrayDesignAttribute adAttribute) {
        setPosition(adAttribute);
        assertTermSourceIsValid(adAttribute);
    }

    @MageTabCheck(value = "A normalization node should have a name", modality = WARNING)
    public void normalizationNodeShouldHaveName(SdrfNormalizationNode normalizationNode) {
        assertNotEmptyName(normalizationNode);
    }

    @MageTabCheck(value = "A scan node should have name specified", modality = WARNING)
    public void scanNodeShouldHaveName(SdrfScanNode scanNode) {
        assertNotEmptyName(scanNode);
    }

    @MageTabCheck("An array data node must have a name")
    public void arrayDataNodeMustHaveName(SdrfArrayDataNode arrayDataNode) {
        assertNotEmptyName(arrayDataNode);
    }

    @MageTabCheck("Name of an array data node must be a valid file location")
    public void nameOfArrayDataNodeMustBeValidFileLocation(SdrfArrayDataNode arrayDataNode) {
        assertFileLocationIsValid(arrayDataNode);
    }

    @MageTabCheck(value = "An array data node should be described by a protocol", modality = WARNING)
    public void arrayDataNodeShouldBeDescribedByProtocol(SdrfArrayDataNode arrayDataNode) {
        setPosition(arrayDataNode);
        assertNodeIsDescribedByProtocol(arrayDataNode);
    }

    @MageTabCheck("A derived array data node must have name specified")
    public void derivedArrayDataNodeMustHaveName(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        assertNotEmptyName(derivedArrayDataNode);
    }

    @MageTabCheck("Name of a derived array data node must be a valid file location")
    public void nameOfDerivedArrayDataNodeMustBeValidFileLocation(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        assertFileLocationIsValid(derivedArrayDataNode);
    }

    @MageTabCheck(value = "A derived array data node should be described by a protocol", modality = WARNING)
    public void derivedArrayDataNodeShouldBeDescribedByProtocol(SdrfDerivedArrayDataNode derivedArrayDataNode) {
        assertNodeIsDescribedByProtocol(derivedArrayDataNode);
    }

    @MageTabCheck("An array data matrix node must have name specified")
    public void arrayDataMatrixNodeMustHaveName(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        assertNotEmptyName(arrayDataMatrixNode);
    }

    @MageTabCheck("Name of an array data matrix node must be valid file location")
    public void nameOfArrayDataMatrixNodeMustBeValidFileLocation(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        assertFileLocationIsValid(arrayDataMatrixNode);
    }

    @MageTabCheck(value = "An array data matrix node should be described by a protocol", modality = WARNING)
    public void arrayDataMatrixNodeShouldBeDescribedByProtocol(SdrfArrayDataMatrixNode arrayDataMatrixNode) {
        assertNodeIsDescribedByProtocol(arrayDataMatrixNode);
    }

    @MageTabCheck("A derived array data matrix node must have name specified")
    public void derivedArrayDataMatrixNodeMustHaveName(SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        assertNotEmptyName(derivedArrayDataMatrixNode);
    }

    @MageTabCheck("Name of derived data matrix node must be valid file location")
    public void nameOfDerivedArrayDataMatrixNodeMustBeValidFileLocation(
            SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        assertFileLocationIsValid(derivedArrayDataMatrixNode);
    }

    @MageTabCheck(value = "A derived array data matrix node should be described by protocol", modality = WARNING)
    public void derivedArrayDataMatrixNodeShouldBeDescribedByProtocol(
            SdrfDerivedArrayDataMatrixNode derivedArrayDataMatrixNode) {
        assertNodeIsDescribedByProtocol(derivedArrayDataMatrixNode);
    }

    private static <T> void assertNotNull(T obj) {
        assertThat(obj, notNullValue());
    }

    private static void assertNotEmptyString(String str) {
        assertThat(str, not(isEmptyOrNullString()));
    }

    private static void assertTermSourceIsValid(HasTermSource t) {
        if (isNullOrEmpty(t.getTermSourceRef())) {
            return;
        }
        assertNotNull(t.getTermSource());
    }

    private static void assertNotEmptyName(SdrfGraphEntity node) {
        setPosition(node);
        assertNotEmptyString(node.getName());
    }

    private static void assertNodeIsDescribedByProtocol(SdrfGraphNode node) {
        Collection<? extends SdrfGraphNode> parents = node.getParentNodes();
        if (parents.isEmpty()) {
            return;
        }
        setPosition(node);
        SdrfGraphNode protocolNode = null;
        for (SdrfGraphNode p : parents) {
            if (SdrfProtocolNode.class.isAssignableFrom(p.getClass())) {
                protocolNode = p;
                break;
            }
        }
        assertNotNull(protocolNode);
    }

    private static void assertFileLocationIsValid(SdrfDataNode dataNode) {
        FileLocation location = dataNode.getLocation();
        if (!location.isEmpty()) {
            setPosition(dataNode);
            assertThat(location, isValidFileLocation());
        }
    }

    private static <T extends HasLocation> void setPosition(T t) {
        setCheckPosition(t.getFileName(), t.getLine(), t.getColumn());
    }
}

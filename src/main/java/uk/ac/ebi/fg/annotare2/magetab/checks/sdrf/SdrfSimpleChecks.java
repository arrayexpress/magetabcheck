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

package uk.ac.ebi.fg.annotare2.magetab.checks.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.UnitAttribute;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.Cell;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Info;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.*;

import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckModality.WARNING;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckPositionKeeper.setCheckPosition;
import static uk.ac.ebi.fg.annotare2.magetab.checker.matchers.IsDateString.isDateString;
import static uk.ac.ebi.fg.annotare2.magetab.checks.idf.IdfConstants.DATE_FORMAT;
import static uk.ac.ebi.fg.annotare2.magetab.extension.KnownTermSource.NCBI_TAXONOMY;

/**
 * @author Olga Melnichuk
 */
public class SdrfSimpleChecks {

    @MageTabCheck("A source node must have name specified")
    public void sourceNodeMustHaveName(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        assertNotEmptyString(sourceNode.getName());
    }

    @MageTabCheck(value = "A source node should have Material Type attribute specified", modality = WARNING)
    public void sourceNodeShouldHaveMaterialTypeAttribute(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        assertNotNull(sourceNode.getMaterialType());
    }

    @MageTabCheck(value = "A source node should have Provider attribute specified", modality = WARNING)
    public void sourceNodeShouldHaveProviderAttribute(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        assertNotNull(sourceNode.getProvider());
        assertNotEmptyString(sourceNode.getProvider().getValue());
    }

    @MageTabCheck("A source node must have an Organism characteristic specified")
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
                if (ts != null && NCBI_TAXONOMY.matches(ts.getFile().getValue())) {
                    return attr;
                }
            }
        }
        return null;
    }

    @MageTabCheck(value = "A source node should have more than 2 characteristics", modality = WARNING)
    public void sourceNodeShouldHaveMoreThan2Characteristics(SdrfSourceNode sourceNode) {
        setPosition(sourceNode);
        Collection<SdrfCharacteristicAttribute> characteristics = sourceNode.getCharacteristics();
        assertNotNull(characteristics);
        assertThat(characteristics.size(), greaterThanOrEqualTo(2));
    }

    @MageTabCheck(value = "A source node should be described by a protocol", modality = WARNING)
    public void sourceNodeShouldBeDescribedByProtocol(SdrfSourceNode sourceNode) {
        assertDescribedByProtocol(sourceNode);
    }

    @MageTabCheck("A sample node must have name specified")
    public void sampleNodeMustHaveName(SdrfSampleNode sampleNode) {
        setPosition(sampleNode);
        assertNotEmptyString(sampleNode.getName());
    }

    @MageTabCheck(value = "A sample node should have Material Type attribute specified", modality = WARNING)
    public void sampleNodeShouldHaveMaterialTypeAttribute(SdrfSampleNode sampleNode) {
        setPosition(sampleNode);
        assertNotNull(sampleNode.getMaterialType());
    }

    @MageTabCheck(value = "A sample node should be described by a protocol", modality = WARNING)
    public void sampleNodeShouldBeDescribedByProtocol(SdrfSampleNode sampleNode) {
        assertDescribedByProtocol(sampleNode);
    }

    @MageTabCheck("An extract node must have name specified")
    public void extractNodeMustHaveName(SdrfExtractNode extractNode) {
        setPosition(extractNode);
        assertNotEmptyString(extractNode.getName());
    }

    @MageTabCheck(value = "An extract node should have Material Type attribute specified", modality = WARNING)
    public void extractNodeShouldHaveMaterialTypeAttribute(SdrfExtractNode extractNode) {
        setPosition(extractNode);
        assertNotNull(extractNode.getMaterialType());
    }

    @MageTabCheck(value = "An extract node should be described by a protocol", modality = WARNING)
    public void extractNodeShouldBeDescribedByProtocol(SdrfExtractNode extractNode) {
        assertDescribedByProtocol(extractNode);
    }

    @MageTabCheck("A labeled extract node must have name specified")
    public void labeledExtractNodeMustHaveName(SdrfLabeledExtractNode labeledExtractNode) {
        setPosition(labeledExtractNode);
        assertNotEmptyString(labeledExtractNode.getName());
    }

    @MageTabCheck(value = "A labeled extract node should have Material Type attribute specified", modality = WARNING)
    public void labeledExtractNodeShouldHaveMaterialTypeAttribute(SdrfLabeledExtractNode labeledExtractNode) {
        setPosition(labeledExtractNode);
        assertNotNull(labeledExtractNode.getMaterialType());
    }

    @MageTabCheck("A labeled extract node must have label attribute specified")
    public void labeledExtractNodeMustHaveLabelAttribute(SdrfLabeledExtractNode labeledExtractNode) {
        setPosition(labeledExtractNode);
        assertNotNull(labeledExtractNode.getLabel());
    }

    @MageTabCheck(value = "A labeled extract node should be described by a protocol", modality = WARNING)
    public void labeledExtractNodeShouldBeDescribedByProtocol(SdrfLabeledExtractNode labeledExtractNode) {
        assertDescribedByProtocol(labeledExtractNode);
    }

    @MageTabCheck(value = "A label attribute should have name specified", modality = WARNING)
    public void labelAttributeShouldHaveName(SdrfLabelAttribute la) {
        setPosition(la);
        assertNotEmptyString(la.getName());
    }

    @MageTabCheck(value = "A label attribute should have TermSource specified", modality = WARNING)
    public void labelAttributeShouldHaveTermSource(SdrfLabelAttribute la) {
        setPosition(la);
        assertNotEmptyString(la.getTermSourceRef());
    }

    @MageTabCheck("TermSource value of label attribute must be defined in IDF")
    public void termSourceOfLabelAttributeMustBeValid(SdrfLabelAttribute la) {
        setPosition(la);
        assertTermSourceIsValid(la);
    }

    @MageTabCheck(value = "A material type attribute should have name specified", modality = WARNING)
    public void materialTypeAttributeShouldHaveName(SdrfMaterialTypeAttribute mta) {
        setPosition(mta);
        assertNotEmptyString(mta.getName());
    }

    @MageTabCheck(value = "A material type attribute should have TermSource specified", modality = WARNING)
    public void materialTypeAttributeShouldHaveTermSource(SdrfMaterialTypeAttribute mta) {
        setPosition(mta);
        assertNotEmptyString(mta.getTermSourceRef());
    }

    @MageTabCheck("TermSource value of material type attribute must be defined in IDF")
    public void termSourceOfMaterialTypeAttributeMustBeValid(SdrfMaterialTypeAttribute mta) {
        setPosition(mta);
        assertTermSourceIsValid(mta);
    }

    @MageTabCheck("A protocol node must have name specified")
    public void protocolNodeMustHaveName(SdrfProtocolNode protocolNode) {
        setPosition(protocolNode);
        assertNotEmptyString(protocolNode.getName());
    }

    @MageTabCheck(value = "A protocol node should have date specified", modality = WARNING)
    public void protocolNodeShouldHaveDate(SdrfProtocolNode protocolNode) {
        setPosition(protocolNode);
        assertNotEmptyString(protocolNode.getDate());
    }

    @MageTabCheck(value = "A protocol node should have TermSource specified", modality = WARNING)
    public void protocolNodeShouldHaveTermSource(SdrfProtocolNode protocolNode) {
        setPosition(protocolNode);
        assertNotEmptyString(protocolNode.getTermSourceRef());
    }

    @MageTabCheck("TermSource value of protocol node must be defined in IDF")
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

    @MageTabCheck("An assay node must have a name")
    public void assayNodeMustHaveName(SdrfAssayNode assayNode) {
        setPosition(assayNode);
        assertNotEmptyString(assayNode.getName());
    }

    @MageTabCheck("An assay node must have Technology Type attribute specified")
    public void assayNodeMustHaveTechnologyTypeAttribute(SdrfAssayNode assayNode) {
        setPosition(assayNode);
        assertNotNull(assayNode.getTechnologyType());
    }

    @MageTabCheck("Technology type attribute must have a name")
    public void technologyTypeMustHaveName(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        setPosition(technologyTypeAttribute);
        assertNotEmptyString(technologyTypeAttribute.getName());
    }

    @MageTabCheck(value = "Technology type attribute should have TermSource specified")
    public void technologyTypeShouldHaveTermSource(SdrfTechnologyTypeAttribute technologyTypeAttribute) {
        setPosition(technologyTypeAttribute);
        assertNotEmptyString(technologyTypeAttribute.getTermSourceRef());
    }

    @MageTabCheck("TermSource of a technology type attribute must be defined in IDF")
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

    @MageTabCheck(value = "A unit attribute should have TermSource specified", modality = WARNING)
    public void unitAttributeShouldHaveTermSource(SdrfUnitAttribute unitAttribute) {
        setPosition(unitAttribute);
        assertNotEmptyString(unitAttribute.getTermSourceRef());
    }

    @MageTabCheck("TermSource value of a unit attribute must be declared in IDF")
    public void termSourceOfUnitAttributeMustBeValid(SdrfUnitAttribute unitAttribute) {
        setPosition(unitAttribute);
        assertTermSourceIsValid(unitAttribute);
    }

    @MageTabCheck(value = "A characteristic attribute should have name specified", modality = WARNING)
    public void characteristicAttributeShouldHaveName(SdrfCharacteristicAttribute attribute) {
        setPosition(attribute);
        assertNotEmptyString(attribute.getType());
    }

    @MageTabCheck(value = "A characteristic attribute should have TermSource specified", modality = WARNING)
    public void characteristicAttributeShouldHaveTermSource(SdrfCharacteristicAttribute attribute){
        setPosition(attribute);
        assertNotEmptyString(attribute.getTermSourceRef());
    }

    @MageTabCheck("TermSource value of a characteristic attribute must be declared in IDF")
    public void termSourceOfCharacteristicAttributeMustBeValid(SdrfCharacteristicAttribute attribute) {
        setPosition(attribute);
        assertTermSourceIsValid(attribute);
    }

    @MageTabCheck(value = "A factor value attribute should have name specified", modality = WARNING)
    public void factorValueAttributeShouldHaveName(SdrfFactorValueAttribute fvAttribute) {
        setPosition(fvAttribute);
        assertNotEmptyString(fvAttribute.getType());
    }

    @MageTabCheck(value = "A factor value attribute should have TermSource specified", modality = WARNING)
    public void factorValueAttributeShouldHaveTermSource(SdrfFactorValueAttribute fvAttribute) {
        setPosition(fvAttribute);
        assertNotEmptyString(fvAttribute.getTermSourceRef());
    }

    @MageTabCheck("TermSource value of a factor value attribute must be declared in IDF")
    public void termSourceOfFactorValueAttributeMustBeValid(SdrfFactorValueAttribute fvAttribute) {
        setPosition(fvAttribute);
        assertTermSourceIsValid(fvAttribute);
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

    private static void assertDescribedByProtocol(SdrfMaterialNode node) {
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

    private static <T extends HasLocation> void setPosition(T t) {
        setCheckPosition(t.getLine(), t.getColumn());
    }
}

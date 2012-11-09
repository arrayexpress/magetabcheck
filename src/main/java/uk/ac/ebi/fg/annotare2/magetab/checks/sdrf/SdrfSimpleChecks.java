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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.Location;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.SDRFLayout;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.MaterialTypeAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.HasLocation;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfCharacteristicAttribute;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfSourceNode;

import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckModality.WARNING;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckPositionKeeper.setCheckPosition;
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
            if ("Organism".equalsIgnoreCase(attr.getName())) {
                TermSource ts = attr.getTermSource();
                if (ts != null && NCBI_TAXONOMY.equalsTo(ts.getFile().getValue())) {
                    return attr;
                }
            }
        }
        return null;
    }

    @MageTabCheck(value = "A material type attribute should have name specified", modality = WARNING)
    public void materialTypeAttributeShouldHaveName(MaterialTypeAttribute mta, SDRFLayout layout) {
        setPosition(mta, layout);
        assertNotEmptyString(mta.getNodeName());
    }

    @MageTabCheck(value = "A material type attribute should have TermSource specified", modality = WARNING)
    public void materialTypeAttributeShouldHaveTermSource(MaterialTypeAttribute mta, SDRFLayout layout) {
        setPosition(mta, layout);
        assertNotEmptyString(mta.termSourceREF);
    }

    @MageTabCheck("TermSource value of material type attribute must be defined in IDF")
    public void termSourceOfMaterialTypeAttributeMustBeValid(MaterialTypeAttribute mta, SDRFLayout layout, IdfData idf) {
        String termSourceRef = mta.termSourceREF;
        if (isNullOrEmpty(termSourceRef)) {
            return;
        }
        setPosition(mta, layout);
        assertNotNull(idf.getTermSource(termSourceRef));
    }

    private static <T> void assertNotNull(T obj) {
        assertThat(obj, notNullValue());
    }

    private static void assertNotEmptyString(String str) {
        assertThat(str, not(isEmptyOrNullString()));
    }

    private static <T extends HasLocation> void setPosition(T t) {
        setCheckPosition(t.getLine(), t.getColumn());
    }

    private static <T extends SDRFAttribute> void setPosition(T attr, SDRFLayout layout) {
        Collection<Location> locations = layout.getLocationsForAttribute(attr);
        Location loc = locations.iterator().next();
        setCheckPosition(loc.getLineNumber(), loc.getColumn());
    }
}

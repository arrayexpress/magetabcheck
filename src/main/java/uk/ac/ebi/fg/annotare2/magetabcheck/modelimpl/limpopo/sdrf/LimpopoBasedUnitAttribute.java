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

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.UnitAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfUnitAttribute;

import java.util.Collection;

import static java.util.Collections.emptyList;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedUnitAttribute extends LimpopoBasedSdrfAttribute<UnitAttribute> implements SdrfUnitAttribute {

    public LimpopoBasedUnitAttribute(UnitAttribute attribute, SdrfHelper helper) {
        super(attribute, helper);
    }

    @Override
    public String getType() {
        return attr().type;
    }

    @Override
    public String getTermSourceRef() {
        return attr().termSourceREF;
    }

    @Override
    public TermSource getTermSource() {
        return termSource(attr().termSourceREF);
    }

    @Override
    protected Collection<SDRFAttribute> getRawAttributes() {
        return emptyList();
    }
}

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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.Location;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphAttribute;

/**
 * @author Olga Melnichuk
 */
abstract class LimpopoBasedSdrfAttribute<T extends SDRFAttribute> extends ObjectWithAttributes implements SdrfGraphAttribute {

    private final SdrfHelper helper;

    private final T attribute;

    private final Location location;

    protected LimpopoBasedSdrfAttribute(T attribute, SdrfHelper helper) {
        super(helper);
        this.helper = helper;
        this.attribute = attribute;
        this.location = helper.getLocation(attribute);
    }

    @Override
    public String getName() {
        return attribute.getAttributeType();
    }

    @Override
    public String getValue() {
        return attribute.getAttributeValue();
    }

    @Override
    public int getLine() {
        return location.getLineNumber();
    }

    @Override
    public int getColumn() {
        return location.getColumn();
    }

    @Override
    public String getFileName() {
        return helper.getSourceName();
    }

    protected TermSource termSource(String termSourceRef) {
        return helper.termSource(termSourceRef);
    }

    protected T attr() {
        return attribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LimpopoBasedSdrfAttribute that = (LimpopoBasedSdrfAttribute) o;

        if (attribute != null ? !attribute.equals(that.attribute) : that.attribute != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return attribute != null ? attribute.hashCode() : 0;
    }
}

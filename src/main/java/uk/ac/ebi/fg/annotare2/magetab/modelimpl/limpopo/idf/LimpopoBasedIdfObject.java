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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.idf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public abstract class LimpopoBasedIdfObject {

    private final IdfHelper helper;

    private final int index;

    public LimpopoBasedIdfObject(@Nonnull IdfHelper helper) {
        this(helper, 0);
    }

    public LimpopoBasedIdfObject(@Nonnull IdfHelper helper, int index) {
        this.helper = helper;
        this.index = index;
    }

    protected IDF idf() {
        return helper.idf();
    }

    protected TermSource termSource(String name) {
        return helper.getTermSource(name);
    }

    protected String get(List<String> list) {
        return list == null || index >= list.size() ? null : list.get(index);
    }

    protected int getColumn() {
        return index < 0 ? index : index + 2;
    }
}
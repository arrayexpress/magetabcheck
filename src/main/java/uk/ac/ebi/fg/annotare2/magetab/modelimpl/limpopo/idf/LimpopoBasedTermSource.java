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

import uk.ac.ebi.fg.annotare2.magetab.model.Cell;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedTermSource extends LimpopoBasedIdfObject implements TermSource {

    public LimpopoBasedTermSource(IdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getName() {
        return createCell(
                get(idf().termSourceName),
                idf().getLayout().getLineNumberForHeader(IdfTags.TERM_SOURCE_NAME));
    }

    @Override
    public Cell<String> getVersion() {
        return createCell(
                get(idf().termSourceVersion),
                idf().getLayout().getLineNumberForHeader(IdfTags.TERM_SOURCE_VERSION));
    }

    @Override
    public Cell<String> getFile() {
        return createCell(
                get(idf().termSourceFile),
                idf().getLayout().getLineNumberForHeader(IdfTags.TERM_SOURCE_FILE));
    }
}

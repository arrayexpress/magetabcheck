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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Comment;

import javax.annotation.Nonnull;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedComment extends LimpopoBasedIdfObject implements Comment {

    private final String type;

    private final String value;

    public LimpopoBasedComment(@Nonnull IdfHelper helper, int index, String type, String value) {
        super(helper, index);
        this.type = type;
        this.value = value;
    }

    @Override
    public String getName() {
        return type;
    }

    @Override
    public Cell<String> getValue() {
        return createCell(
                value,
                idf().getLayout().getLineNumberForComment(type));
    }
}

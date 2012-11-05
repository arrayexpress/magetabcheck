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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.fg.annotare2.magetab.model.Cell;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.QualityControlType;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedQualityControlType extends LimpopoBasedIdfObject implements QualityControlType {


    public LimpopoBasedQualityControlType(@Nonnull LimpopoIdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getName() {
        return new Cell<String>(
                get(idf().qualityControlType),
                idf().getLayout().getLineNumberForHeader(IdfTags.QUALITY_CONTROL_TYPE),
                getColumn());
    }

    @Override
    public Cell<String> getAccession() {
        return new Cell<String>(
                get(idf().qualityControlTermAccession),
                idf().getLayout().getLineNumberForHeader(IdfTags.QUALITY_CONTROL_TERM_ACCESSION_NUMBER),
                getColumn());
    }

    @Override
    public Cell<TermSource> getSource() {
        return new Cell<TermSource>(
                termSource(get(idf().qualityControlTermSourceREF)),
                idf().getLayout().getLineNumberForHeader(IdfTags.QUALITY_CONTROL_TERM_SOURCE_REF),
                getColumn());
    }
}

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

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.QualityControlType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;

import javax.annotation.Nonnull;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedQualityControlType extends LimpopoBasedIdfObject implements QualityControlType {


    public LimpopoBasedQualityControlType(@Nonnull IdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getName() {
        return createCell(
                get(idf().qualityControlType),
                idf().getLayout().getLineNumberForHeader(IdfTags.QUALITY_CONTROL_TYPE));
    }

    @Override
    public Cell<String> getAccession() {
        return createCell(
                get(idf().qualityControlTermAccession),
                idf().getLayout().getLineNumberForHeader(IdfTags.QUALITY_CONTROL_TERM_ACCESSION_NUMBER));
    }

    @Override
    public Cell<TermSource> getSource() {
        return createCell(
                termSource(get(idf().qualityControlTermSourceREF)),
                idf().getLayout().getLineNumberForHeader(IdfTags.QUALITY_CONTROL_TERM_SOURCE_REF));
    }
}

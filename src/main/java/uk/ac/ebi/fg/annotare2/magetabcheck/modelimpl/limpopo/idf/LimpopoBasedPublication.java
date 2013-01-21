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
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Publication;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.PublicationStatus;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;

import javax.annotation.Nonnull;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedPublication extends LimpopoBasedIdfObject implements Publication {

    public LimpopoBasedPublication(@Nonnull IdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getPubMedId() {
        return createCell(
                get(idf().pubMedId),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_PUB_MED_ID));
    }

    @Override
    public Cell<String> getPublicationDOI() {
        return createCell(
                get(idf().publicationDOI),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_DOI));
    }

    @Override
    public Cell<String> getAuthorList() {
        return createCell(
                get(idf().publicationAuthorList),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_AUTHOR_LIST));
    }

    @Override
    public Cell<String> getTitle() {
        return createCell(
                get(idf().publicationTitle),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_TITLE));
    }

    @Override
    public PublicationStatus getStatus() {
        return new PublicationStatus() {
            @Override
            public Cell<String> getName() {
                return createCell(
                        get(idf().publicationStatus),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_STATUS));
            }

            @Override
            public Cell<String> getAccession() {
                return createCell(
                        get(idf().publicationStatusTermAccession),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_STATUS_TERM_ACCESSION_NUMBER));
            }

            @Override
            public Cell<TermSource> getSource() {
                return createCell(
                        termSource(get(idf().publicationStatusTermSourceREF)),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_STATUS_TERM_SOURCE_REF));
            }
        };
    }
}

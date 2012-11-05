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
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Publication;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.PublicationStatus;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedPublication extends LimpopoBasedIdfObject implements Publication {

    public LimpopoBasedPublication(@Nonnull LimpopoIdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getPubMedId() {
        return new Cell<String>(
                get(idf().pubMedId),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_PUB_MED_ID),
                getColumn());
    }

    @Override
    public Cell<String> getPublicationDOI() {
        return new Cell<String>(
                get(idf().publicationDOI),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_DOI),
                getColumn());
    }

    @Override
    public Cell<String> getAuthorList() {
        return new Cell<String>(
                get(idf().publicationAuthorList),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_AUTHOR_LIST),
                getColumn());
    }

    @Override
    public Cell<String> getTitle() {
        return new Cell<String>(
                get(idf().publicationTitle),
                idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_TITLE),
                getColumn());
    }

    @Override
    public PublicationStatus getStatus() {
        return new PublicationStatus() {
            @Override
            public Cell<String> getName() {
                return new Cell<String>(
                        get(idf().publicationStatus),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_STATUS),
                        getColumn());
            }

            @Override
            public Cell<String> getAccession() {
                return new Cell<String>(
                        get(idf().publicationStatusTermAccession),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_STATUS_TERM_ACCESSION_NUMBER),
                        getColumn());
            }

            @Override
            public Cell<TermSource> getSource() {
                return new Cell<TermSource>(
                        termSource(get(idf().publicationStatusTermSourceREF)),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PUBLICATION_STATUS_TERM_SOURCE_REF),
                        getColumn());
            }
        };
    }
}

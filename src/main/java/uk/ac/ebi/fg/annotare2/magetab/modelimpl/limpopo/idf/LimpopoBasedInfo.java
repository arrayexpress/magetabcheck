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
import uk.ac.ebi.fg.annotare2.magetab.model.FileLocation;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Info;

import javax.annotation.Nonnull;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.idf.IdfTags.*;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedInfo extends LimpopoBasedIdfObject implements Info {

    public LimpopoBasedInfo(@Nonnull IdfHelper helper) {
        super(helper);
    }

    @Override
    public Cell<String> getTitle() {
        return createCell(
                idf().investigationTitle,
                idf().getLayout().getLineNumberForHeader(INVESTIGATION_TITLE));
    }

    @Override
    public Cell<String> getExperimentDescription() {
        return createCell(
                idf().experimentDescription,
                idf().getLayout().getLineNumberForHeader(EXPERIMENT_DESCRIPTION));
    }

    @Override
    public Cell<String> getDateOfExperiment() {
        return createCell(
                idf().dateOfExperiment,
                idf().getLayout().getLineNumberForHeader(DATE_OF_EXPERIMENT));
    }

    @Override
    public Cell<String> getPublicReleaseDate() {
        return createCell(
                idf().publicReleaseDate,
                idf().getLayout().getLineNumberForHeader(PUBLIC_RELEASE_DATE));
    }

    @Override
    public Cell<FileLocation> getSdrfFile() {
        List<String> sdrfFiles = idf().sdrfFile;
        return createCell(
                new FileLocation(idf().getLocation(),
                        (sdrfFiles == null || sdrfFiles.isEmpty()) ? null : sdrfFiles.get(0)),
                idf().getLayout().getLineNumberForHeader(SDRF_FILE));
    }
}

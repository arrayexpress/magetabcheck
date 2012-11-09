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
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Info;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Location;

import javax.annotation.Nonnull;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.IdfTags.*;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedInfo extends LimpopoBasedIdfObject implements Info {

    public LimpopoBasedInfo(@Nonnull IdfHelper helper) {
        super(helper);
    }

    @Override
    public Cell<String> getTitle() {
        return new Cell<String>(
                idf().investigationTitle,
                idf().getLayout().getLineNumberForHeader(INVESTIGATION_TITLE),
                getColumn());
    }

    @Override
    public Cell<String> getExperimentDescription() {
        return new Cell<String>(
                idf().experimentDescription,
                idf().getLayout().getLineNumberForHeader(EXPERIMENT_DESCRIPTION),
                getColumn());
    }

    @Override
    public Cell<String> getDateOfExperiment() {
        return new Cell<String>(
                idf().dateOfExperiment,
                idf().getLayout().getLineNumberForHeader(DATE_OF_EXPERIMENT),
                getColumn());
    }

    @Override
    public Cell<String> getPublicReleaseDate() {
        return new Cell<String>(
                idf().publicReleaseDate,
                idf().getLayout().getLineNumberForHeader(PUBLIC_RELEASE_DATE),
                getColumn());
    }

    @Override
    public Cell<Location> getSdrfFile() {
        List<String> sdrfFiles = idf().sdrfFile;
        return new Cell<Location>(
                new Location(idf().getLocation(),
                        (sdrfFiles == null || sdrfFiles.isEmpty()) ? null : sdrfFiles.get(0)),
                idf().getLayout().getLineNumberForHeader(SDRF_FILE),
                getColumn());
    }
}

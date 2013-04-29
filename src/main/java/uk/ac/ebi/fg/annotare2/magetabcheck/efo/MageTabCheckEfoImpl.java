/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.services.efo.EfoNode;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

/**
 * @author Olga Melnichuk
 */
public class MageTabCheckEfoImpl implements MageTabCheckEfo {

    private final EfoService efoService;

    @Inject
    public MageTabCheckEfoImpl(EfoService efoService) {
        this.efoService = efoService;
    }

    @Override
    public String findHtsInvestigationType(String name) {
        EfoNode node = efoService.findTermByName(HTS_EXPERIMENT_TYPES, name);
        return node == null ? null : node.getAccession();
    }

    @Override
    public String findMaInvestigationType(String name) {
        EfoNode node = efoService.findTermByName(MA_EXPERIMENT_TYPES, name);
        return node == null ? null : node.getAccession();
    }

    @Override
    public boolean isLibraryConstructionProtocol(String accession, String name) {
        return efoService.findTermByNameOrAccession(name, accession, LIBRARY_CONSTRUCTION_PROTOCOL) != null;
    }

    @Override
    public boolean isSequencingProtocol(String accession, String name) {
        return efoService.findTermByNameOrAccession(name, accession, SEQUENCING_PROTOCOL) != null;
    }
}

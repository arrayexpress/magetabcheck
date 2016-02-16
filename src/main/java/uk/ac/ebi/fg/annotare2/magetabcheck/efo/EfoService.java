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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import uk.ac.ebi.fg.annotare2.magetabcheck.ServiceUnavailableException;

/**
 * @author Olga Melnichuk
 */
public interface EfoService {

    public static final EfoService UNAVAILABLE = new EfoService() {
        @Override
        public EfoTerm findTermByAccession(String accession) {
            throw unavailable();
        }

        @Override
        public EfoTerm findTermByLabel(String name, String rootAccession) {
            throw unavailable();
        }

        @Override
        public EfoTerm findTermByAccession(String accession, String rootAccession) {
            throw unavailable();
        }

        @Override
        public EfoTerm findTermByLabelOrAccession(String name, String accession, String rootAccession) {
            throw unavailable();
        }

        private ServiceUnavailableException unavailable() {
            return new ServiceUnavailableException("EFO Service hasn't been started properly. See logs for details.");
        }
    };

    EfoTerm findTermByLabel(String name, String rootAccession);

    EfoTerm findTermByAccession(String accession);

    EfoTerm findTermByAccession(String accession, String rootAccession);

    EfoTerm findTermByLabelOrAccession(String name, String accession, String rootAccession);
}

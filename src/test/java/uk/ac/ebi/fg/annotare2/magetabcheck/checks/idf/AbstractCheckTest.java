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

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public abstract class AbstractCheckTest {

    protected static Protocol createProtocol(final String name,
                                           final String description,
                                           final List<String> params,
                                           final String hardware,
                                           final String software,
                                           final String contact,
                                           final String type,
                                           final TermSource typeSource) {
        return new Protocol() {
            @Override
            public Cell<String> getName() {
                return createCell(name);
            }

            @Override
            public Cell<String> getDescription() {
                return createCell(description);
            }

            @Override
            public Cell<List<String>> getParameters() {
                return createCell(params);
            }

            @Override
            public Cell<String> getHardware() {
                return createCell(hardware);
            }

            @Override
            public Cell<String> getSoftware() {
                return createCell(software);
            }

            @Override
            public Cell<String> getContact() {
                return createCell(contact);
            }

            @Override
            public ProtocolType getType() {
                return new ProtocolType() {
                    @Override
                    public Cell<String> getName() {
                        return createCell(type);
                    }

                    @Override
                    public Cell<String> getAccession() {
                        return createCell("");
                    }

                    @Override
                    public Cell<TermSource> getSource() {
                        return createCell(typeSource);
                    }
                };
            }
        };
    }

    protected static TermSource createTermSource(final String name, final String url) {
        return new TermSource() {
            @Override
            public Cell<String> getName() {
                return createCell(name);
            }

            @Override
            public Cell<String> getVersion() {
                return createCell("");
            }

            @Override
            public Cell<String> getFile() {
                return createCell(url);
            }
        };
    }

    protected static <T> Cell<T> createCell(T value) {
        return new Cell<T>(value, "", 0, 0);
    }
}

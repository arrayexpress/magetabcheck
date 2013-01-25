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

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownProtocolHardware;
import uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownTermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;

/**
 * @author Olga Melnichuk
 */
public class SequencingProtocolRequiredTest {

    private static final String SEQUENCING_PROTOCOL_TYPE = "sequencing protocol";

    @Test
    public void testValidSeqProtocol() {
        SequencingProtocolRequired rule = new SequencingProtocolRequired(efoServiceMock());
        rule.visit(createProtocol(
                "name",
                "description",
                Collections.<String>emptyList(),
                KnownProtocolHardware.LIST.get(0),
                "software",
                "contact",
                SEQUENCING_PROTOCOL_TYPE,
                createTermSource(
                        KnownTermSource.EFO.name(),
                        KnownTermSource.EFO.getUrl())));
        rule.check();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidProtocolName() {
        SequencingProtocolRequired rule = new SequencingProtocolRequired(efoServiceMock());
        rule.visit(createProtocol(
                "name",
                "description",
                Collections.<String>emptyList(),
                KnownProtocolHardware.LIST.get(0),
                "software",
                "contact",
                "invalid protocol type",
                createTermSource(
                        KnownTermSource.EFO.name(),
                        KnownTermSource.EFO.getUrl())));
        rule.check();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidHardware() {
        SequencingProtocolRequired rule = new SequencingProtocolRequired(efoServiceMock());
        rule.visit(createProtocol(
                "name",
                "description",
                Collections.<String>emptyList(),
                "invalid hardware name",
                "software",
                "contact",
                SEQUENCING_PROTOCOL_TYPE,
                createTermSource(
                        KnownTermSource.EFO.name(),
                        KnownTermSource.EFO.getUrl())));
        rule.check();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidProtocolTypeSource() {
        SequencingProtocolRequired rule = new SequencingProtocolRequired(efoServiceMock());
        rule.visit(createProtocol(
                "name",
                "description",
                Collections.<String>emptyList(),
                KnownProtocolHardware.LIST.get(0),
                "software",
                "contact",
                SEQUENCING_PROTOCOL_TYPE,
                createTermSource(
                        "not efo",
                        "unknown url")));
        rule.check();
    }

    private EfoService efoServiceMock() {
        EfoService mock = EasyMock.createMock(EfoService.class);
        final Capture<String> argCapture = new Capture<String>();
        expect(mock.isSequencingProtocol((String) anyObject(), capture(argCapture))).andAnswer(
                new IAnswer<Boolean>() {
                    @Override
                    public Boolean answer() throws Throwable {
                        String value = argCapture.getValue();
                        return value != null && value.equals("sequencing protocol");
                    }
                }
        );
        replay(mock);
        return mock;
    }

    private static Protocol createProtocol(final String name,
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

    private static TermSource createTermSource(final String name, final String url) {
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

    private static <T> Cell<T> createCell(T value) {
        return new Cell<T>(value, "", 0, 0);
    }
}

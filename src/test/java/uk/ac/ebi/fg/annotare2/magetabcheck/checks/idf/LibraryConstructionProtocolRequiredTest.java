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
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import java.util.Collections;

import static org.easymock.EasyMock.*;

/**
 * @author Olga Melnichuk
 */
public class LibraryConstructionProtocolRequiredTest extends AbstractCheckTest {

    private static final String LIBRARY_CONSTRUCTION_TYPE = "library construction protocol";

    @Test
    public void testValidLibraryProtocol() {
        LibraryConstructionProtocolRequired rule = new LibraryConstructionProtocolRequired(efoServiceMock());
        rule.visit(createProtocol(
                "name",
                "description",
                Collections.<String>emptyList(),
                KnownProtocolHardware.LIST.get(0),
                "",
                "",
                LIBRARY_CONSTRUCTION_TYPE,
                createTermSource(
                        KnownTermSource.EFO.name(),
                        KnownTermSource.EFO.getUrl())));
        rule.check();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidProtocolType() {
        LibraryConstructionProtocolRequired rule = new LibraryConstructionProtocolRequired(efoServiceMock());
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
    public void testInvalidProtocolTypeSource() {
        LibraryConstructionProtocolRequired rule = new LibraryConstructionProtocolRequired(efoServiceMock());
        rule.visit(createProtocol(
                "name",
                "description",
                Collections.<String>emptyList(),
                KnownProtocolHardware.LIST.get(0),
                "",
                "",
                LIBRARY_CONSTRUCTION_TYPE,
                createTermSource(
                        "not efo",
                        "unknown url")));
        rule.check();
    }

    private EfoService efoServiceMock() {
        EfoService mock = EasyMock.createMock(EfoService.class);
        final Capture<String> argCapture = new Capture<String>();
        expect(mock.isLibraryConstructionProtocol((String) anyObject(), capture(argCapture))).andAnswer(
                new IAnswer<Boolean>() {
                    @Override
                    public Boolean answer() throws Throwable {
                        String value = argCapture.getValue();
                        return value != null && value.equals("library construction protocol");
                    }
                }
        );
        replay(mock);
        return mock;
    }

}

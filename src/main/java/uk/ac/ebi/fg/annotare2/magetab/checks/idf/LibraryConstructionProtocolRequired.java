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

package uk.ac.ebi.fg.annotare2.magetab.checks.idf;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckApplicationType;
import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static uk.ac.ebi.fg.annotare2.magetab.extension.KnownTermSource.EFO;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        value = "Library construction protocol is required for HTS submissions",
        application = CheckApplicationType.HTS_ONLY)
public class LibraryConstructionProtocolRequired {

    private final EfoService efo;

    private int counter;

    @Inject
    public LibraryConstructionProtocolRequired(EfoService efo) {
        this.efo = efo;
    }

    @Visit
    public void visit(Protocol protocol) {
        ProtocolType type = protocol.getType();
        TermSource ts = type.getSource().getValue();
        if (ts == null) {
            return;
        }

        if (!isEfoTermSource(ts)) {
            return;
        }

        if (!isLibraryConstructionProtocol(type)) {
            return;
        }

        counter++;
    }

    private boolean isLibraryConstructionProtocol(ProtocolType type) {
        return efo.isLibraryConstructionProtocol(type.getAccession().getValue(), type.getName().getValue());
    }

    private boolean isEfoTermSource(TermSource ts) {
        return EFO.matches(ts.getFile().getValue());
    }

    @Check
    public void check() {
        assertThat(1, equalTo(counter));
    }
}

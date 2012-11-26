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
import uk.ac.ebi.fg.annotare2.magetab.checker.GlobalCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.extension.KnownTermSource;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        value = "Library construction protocol is required for HTS submissions",
        application = CheckApplicationType.HTS_ONLY)
public class LibraryConstructionProtocolRequired implements GlobalCheck<Protocol> {

    private final EfoService efo;

    private int counter;

    @Inject
    public LibraryConstructionProtocolRequired(EfoService efo) {
        this.efo = efo;
    }

    @Override
    public void visit(Protocol protocol) {
        ProtocolType type  = protocol.getType();
        TermSource ts = type.getSource().getValue();
        if (ts == null) {
            return;
        }

        String accession = type.getAccession().getValue();
        if (KnownTermSource.EFO.matches(ts.getFile().getValue())) {
            if (efo.isLibraryConstructionProtocol(accession, type.getName().getValue())) {
               counter++;
            }
        }
    }

    @Override
    public void check() {
        assertThat(1, equalTo(counter));
    }

}

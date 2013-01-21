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

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static uk.ac.ebi.fg.annotare2.magetab.extension.KnownProtocolHardware.isValidProtocolHardware;
import static uk.ac.ebi.fg.annotare2.magetab.extension.KnownTermSource.EFO;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        value = "Sequencing protocol is required for HTS submissions",
        application = CheckApplicationType.HTS_ONLY)
public class SequencingProtocolRequired {

    private final EfoService efo;

    private int counter = 0;

    @Inject
    public SequencingProtocolRequired(EfoService efo) {
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

        if (!isSequencingProtocol(type)) {
            return;
        }

        if (!hasSequencingHardware(protocol.getHardware().getValue())) {
            return;
        }

        counter++;
    }

    private boolean hasSequencingHardware(String value) {
        if (isNullOrEmpty(value)) {
            return false;
        }
        String[] v = value.trim().split("\\s*,\\s*");
        return isValidProtocolHardware(v);
    }

    private boolean isSequencingProtocol(ProtocolType type) {
        return efo.isSequencingProtocol(type.getAccession().getValue(), type.getName().getValue());
    }

    private boolean isEfoTermSource(TermSource ts) {
        return EFO.matches(ts.getFile().getValue());
    }

    @Check
    public void check() {
        assertThat(1, equalTo(counter));
    }
}

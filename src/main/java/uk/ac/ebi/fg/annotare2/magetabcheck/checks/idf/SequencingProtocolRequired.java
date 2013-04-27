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

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.RangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Ranges.singleton;
import static uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownProtocolHardware.isValidProtocolHardware;
import static uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownTermSource.EFO;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "PR09",
        value = "Sequencing protocol is required for HTS submissions",
        application = CheckApplicationType.HTS_ONLY,
        details = "1. A `Protocol Type` field must be the name of " +
                "['sequencing protocols class' in EFO](http://bioportal.bioontology.org/ontologies/49470/?p=terms&conceptid=efo%3AEFO_0004170) " +
                "or one of its children;<br/>2. `Protocol Term Source REF` must be \"EFO\" ([full list](#term-source-list))" +
                "<br/>3. `Protocol Hardware` field must contain a comma separated list of protocol hardware used ([supported term sources](#protocol-hardware-list));")
public class SequencingProtocolRequired extends RangeCheck<Protocol> {

    @Inject
    public SequencingProtocolRequired(EfoService efo) {
        super(new SequencingProtocolPredicate(efo), singleton(1));
    }

    private static class SequencingProtocolPredicate implements Predicate<Protocol> {
        private final EfoService efo;

        private SequencingProtocolPredicate(EfoService efo) {
            this.efo = efo;
        }

        @Override
        public boolean apply(@Nullable Protocol protocol) {
            return isSequencingProtocol(protocol.getType())
                    && hasSequencingHardware(protocol);
        }

        private boolean hasSequencingHardware(Protocol protocol) {
            String hardware = protocol.getHardware().getValue();
            if (isNullOrEmpty(hardware)) {
                return false;
            }
            String[] v = hardware.trim().split("\\s*,\\s*");
            return isValidProtocolHardware(v);
        }

        private boolean isSequencingProtocol(ProtocolType type) {
            return isEfoTermSource(type.getSource().getValue())
                    && efo.isSequencingProtocol(type.getAccession().getValue(), type.getName().getValue());
        }

        private boolean isEfoTermSource(TermSource ts) {
            return ts != null && EFO.matches(ts.getFile().getValue());
        }
    }
}

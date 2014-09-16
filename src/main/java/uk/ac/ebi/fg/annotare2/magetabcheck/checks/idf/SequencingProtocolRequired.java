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
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.MageTabCheckEfo;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;

import javax.annotation.Nullable;

import static com.google.common.collect.Range.singleton;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "PR09",
        value = "Sequencing protocol is required for HTS submissions",
        application = CheckApplicationType.HTS_ONLY,
        details = "1. A `Protocol Type` field must be the name of " +
                "['sequencing protocols class' in EFO](http://bioportal.bioontology.org/ontologies/49470/?p=terms&conceptid=efo%3AEFO_0004170) " +
                "or one of its children;<br/>2. `Protocol Term Source REF` must be \"EFO\" if specified ([full list](#term-source-list))")
public class SequencingProtocolRequired extends RangeCheck<Protocol> {

    @Inject
    public SequencingProtocolRequired(MageTabCheckEfo efo) {
        super(new SequencingProtocolPredicate(efo), singleton(1));
    }

    static class SequencingProtocolPredicate implements Predicate<Protocol> {
        private final MageTabCheckEfo efo;

        SequencingProtocolPredicate(MageTabCheckEfo efo) {
            this.efo = efo;
        }

        @Override
        public boolean apply(@Nullable Protocol protocol) {
            return protocol != null && efo.isSequencingProtocol(protocol.getType());
        }
    }
}

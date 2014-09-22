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

import static com.google.common.collect.Range.atLeast;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "PR08",
        value = "Library construction protocol is required for HTS submissions",
        application = CheckApplicationType.HTS_ONLY,
        details = "1. A `Protocol Type` field must be the name of " +
                "['library construction protocols' class in EFO](http://bioportal.bioontology.org/ontologies/49470/?p=terms&conceptid=efo%3AEFO_0004184) " +
                "or one of its children; <br/> 2. `Protocol Term Source REF` must be \"EFO\" if specified ([supported term sources](#term-source-list));")
public class LibraryConstructionProtocolRequired extends RangeCheck<Protocol> {

    @Inject
    public LibraryConstructionProtocolRequired(MageTabCheckEfo efo) {
        super(new LibraryConstructionProtocolPredicate(efo), atLeast(1));
    }

    private static class LibraryConstructionProtocolPredicate implements Predicate<Protocol> {

        private final MageTabCheckEfo efo;

        private LibraryConstructionProtocolPredicate(MageTabCheckEfo efo) {
            this.efo = efo;
        }

        @Override
        public boolean apply(@Nullable Protocol protocol) {
            return efo.isLibraryConstructionProtocol(protocol.getType());
        }
    }
}

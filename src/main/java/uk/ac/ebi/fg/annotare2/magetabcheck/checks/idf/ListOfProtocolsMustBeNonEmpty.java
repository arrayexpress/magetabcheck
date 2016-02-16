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

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf;

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.NonEmptyRangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;

import javax.annotation.Nullable;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "PR01",
        value = "At least one protocol must be used in an experiment")
public class ListOfProtocolsMustBeNonEmpty extends NonEmptyRangeCheck<Protocol> {
    public ListOfProtocolsMustBeNonEmpty() {
        super(new Predicate<Protocol>() {
            @Override
            public boolean apply(@Nullable Protocol protocol) {
                return protocol != null &&
                        protocol.getName() != null &&
                        protocol.getName().getValue() != null &&
                        protocol.getDescription()!=null &&
                        protocol.getDescription().getValue() !=null &&
                        protocol.getType() != null &&
                        protocol.getType().getName() != null &&
                        protocol.getType().getName().getValue() != null;
            }
        });
    }
}

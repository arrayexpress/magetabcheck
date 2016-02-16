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
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermList;

import javax.annotation.Nullable;

import static uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf.IdfConstants.SUBMITTER_ROLE;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "C05",
        value = "At least one contact must have '" + SUBMITTER_ROLE + "' role specified ")
public class AtLeastOneContactMustBeSubmitter extends NonEmptyRangeCheck<Person> {

    public AtLeastOneContactMustBeSubmitter() {
        super(new Predicate<Person>() {
            @Override
            public boolean apply(@Nullable Person person) {
                TermList roles = person.getRoles();
                return (roles != null)
                        && !roles.isEmpty()
                        && roles.getNames().getValue().contains(SUBMITTER_ROLE);
            }
        });
    }
}

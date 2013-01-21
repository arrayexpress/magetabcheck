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

import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static uk.ac.ebi.fg.annotare2.magetab.checks.idf.IdfConstants.SUBMITTER_ROLE;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck("At least one contact must have '" + SUBMITTER_ROLE + "' role specified ")
public class AtLeastOneContactMustBeSubmitter {

    private int submitterCount;

    @Visit
    public void visit(Person person) {
        TermList roles = person.getRoles();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        if (roles.getNames().getValue().contains(SUBMITTER_ROLE)) {
            submitterCount++;
        }
    }

    @Check
    public void check() {
        assertThat(submitterCount, greaterThan(0));
    }
}

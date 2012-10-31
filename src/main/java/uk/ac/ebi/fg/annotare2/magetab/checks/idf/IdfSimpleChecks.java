/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

import uk.ac.ebi.fg.annotare2.magetab.checker.CheckApplicationType;
import uk.ac.ebi.fg.annotare2.magetab.checker.InvestigationType;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckModality;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

import static uk.ac.ebi.fg.annotare2.magetab.checks.idf.IdfConstants.SUBMITTER_ROLE;


/**
 * @author Olga Melnichuk
 */
public class IdfSimpleChecks {

    @MageTabCheck("Contact must have Last Name specified")
    public void contactMustHaveLastName(Person person) {
        assertThat(person.getLastName(), not(isEmptyString()));
    }

    @MageTabCheck(value = "Contact should have First Name specified", modality = CheckModality.WARNING)
    public void contactShouldHaveFirstName(Person person) {
        assertThat(person.getFirstName(), not(isEmptyString()));
    }

    @MageTabCheck(value = "Contact should have Affiliation specified", modality = CheckModality.WARNING)
    public void contactShouldHaveAffiliation(Person person) {
        assertThat(person.getAffiliation(), not(isEmptyString()));
    }

    @MageTabCheck(
            value = "Contact with '" + SUBMITTER_ROLE + "' role must have Affiliation specified",
            application = CheckApplicationType.HTS_ONLY)
    public void submitterMustHaveAffiliation(Person person) {
        TermList roles = person.getRoles();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        if (roles.getNames().contains(SUBMITTER_ROLE)) {
            assertThat(person.getAffiliation(), not(isEmptyString()));
        }
    }

}

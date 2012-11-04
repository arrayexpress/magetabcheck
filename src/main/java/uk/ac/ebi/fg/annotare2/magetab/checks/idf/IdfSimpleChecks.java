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
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckModality;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.Cell;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Info;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Location;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckContext.setCheckPosition;
import static uk.ac.ebi.fg.annotare2.magetab.checker.matchers.IsDateString.isDateString;
import static uk.ac.ebi.fg.annotare2.magetab.checker.matchers.IsValidFileLocation.isValidFileLocation;
import static uk.ac.ebi.fg.annotare2.magetab.checks.idf.IdfConstants.DATE_FORMAT;
import static uk.ac.ebi.fg.annotare2.magetab.checks.idf.IdfConstants.SUBMITTER_ROLE;

/**
 * @author Olga Melnichuk
 */
public class IdfSimpleChecks {

    @MageTabCheck("Investigation Title must be specified")
    public void investigationTitleRequired(final Info info) {
        assertNotEmptyString(info.getTitle());
    }

    @MageTabCheck("Experiment Description must be specified")
    public void experimentDescriptionRequired(Info info) {
        assertNotEmptyString(info.getExperimentDescription());
    }

    @MageTabCheck("Public Release Date must be specified")
    public void publicReleaseDateRequired(Info info) {
        assertNotEmptyString(info.getPublicReleaseDate());
    }

    @MageTabCheck("Public Release Date must be in 'YYYY-MM-DD' format")
    public void publicReleaseDateFormat(Info info) {
        Cell<String> cell = info.getPublicReleaseDate();
        if (isNullOrEmpty(cell.getValue())) {
            return;
        }
        setCheckPosition(cell.getLine(), cell.getColumn());
        assertThat(cell.getValue(), isDateString(DATE_FORMAT));
    }

    @MageTabCheck(value = "Date Of Experiment should be specified", modality = CheckModality.WARNING)
    public void dateOfExperimentShouldBeSpecified(Info info) {
        assertNotEmptyString(info.getDateOfExperiment());
    }

    @MageTabCheck("Date Of Experiment must be in 'YYYY-MM-DD' format")
    public void dateOfExperimentFormat(Info info) {
        Cell<String> cell = info.getDateOfExperiment();
        if (isNullOrEmpty(cell.getValue())) {
            return;
        }
        setCheckPosition(cell.getLine(), cell.getColumn());
        assertThat(cell.getValue(), isDateString(DATE_FORMAT));
    }

    @MageTabCheck("SDRF File must be specified")
    public void sdrfFileMustBeSpecified(Info info) {
        Cell<Location> cell = info.getSdrfFile();
        setCheckPosition(cell.getLine(), cell.getColumn());
        assertThat(cell.getValue(), notNullValue());
        assertThat(cell.getValue().isEmpty(), is(FALSE));
    }

    @MageTabCheck("SDRF File must be valid location")
    public void sdrfFileMustBeValidLocation(Info info) {
        Cell<Location> celll = info.getSdrfFile();
        Location loc = celll.getValue();
        if (loc == null || loc.isEmpty()) {
            return;
        }
        setCheckPosition(celll.getLine(), celll.getColumn());
        assertThat(loc, isValidFileLocation());
    }

    @MageTabCheck("A contact must have Last Name specified")
    public void contactMustHaveLastName(Person person) {
        assertNotEmptyString(person.getLastName());
    }

    @MageTabCheck(value = "A contact should have First Name specified", modality = CheckModality.WARNING)
    public void contactShouldHaveFirstName(Person person) {
        assertNotEmptyString(person.getFirstName());
    }

    @MageTabCheck(value = "A contact should have Affiliation specified", modality = CheckModality.WARNING)
    public void contactShouldHaveAffiliation(Person person) {
        assertNotEmptyString(person.getAffiliation());
    }

    @MageTabCheck(value = "A contact roles should have TermSource specified", modality = CheckModality.WARNING)
    public void check(Person person) {
        TermList roles = person.getRoles();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        assertThat(roles.getSource(), notNullValue());
    }

    @MageTabCheck(
            value = "A contact with '" + SUBMITTER_ROLE + "' role must have Affiliation specified",
            application = CheckApplicationType.HTS_ONLY)
    public void submitterMustHaveAffiliation(Person person) {
        TermList roles = person.getRoles();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        if (roles.getNames().getValue().contains(SUBMITTER_ROLE)) {
            assertNotEmptyString(person.getAffiliation());
        }
    }

    private static void assertNotEmptyString(Cell<String> cell) {
        setCheckPosition(cell.getLine(), cell.getColumn());
        assertThat(cell.getValue(), not(isEmptyOrNullString()));
    }
}

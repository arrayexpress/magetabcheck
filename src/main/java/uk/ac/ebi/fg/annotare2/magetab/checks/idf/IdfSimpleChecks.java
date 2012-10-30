package uk.ac.ebi.fg.annotare2.magetab.checks.idf;

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

    @MageTabCheck
    public void contactMustHaveLastName(Person person) {
        assertThat("Contact must have Last Name specified", person.getLastName(), not(isEmptyString()));
    }

    @MageTabCheck(modality = CheckModality.WARNING)
    public void contactShouldHaveFirstName(Person person) {
        assertThat("Contact should have First Name specified", person.getFirstName(), not(isEmptyString()));
    }

    @MageTabCheck(applyTo = InvestigationType.HTS)
    public void submitterMustHaveAffiliation(Person person) {
        TermList roles = person.getRoles();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        if (roles.getNames().contains(SUBMITTER_ROLE)) {
            assertThat("Contact with '" + SUBMITTER_ROLE + "' role must have Affiliation specified", person.getAffiliation(), not(isEmptyString()));
        }
    }

}

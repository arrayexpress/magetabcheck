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

    @MageTabCheck("Contact must have Last Name specified")
    public void contactMustHaveLastName(Person person) {
        assertThat(person.getLastName(), not(isEmptyString()));
    }

    @MageTabCheck(value = "Contact should have First Name specified", modality = CheckModality.WARNING)
    public void contactShouldHaveFirstName(Person person) {
        assertThat(person.getFirstName(), not(isEmptyString()));
    }

    @MageTabCheck(value = "Contact with '" + SUBMITTER_ROLE + "' role must have Affiliation specified", applyTo = InvestigationType.HTS)
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

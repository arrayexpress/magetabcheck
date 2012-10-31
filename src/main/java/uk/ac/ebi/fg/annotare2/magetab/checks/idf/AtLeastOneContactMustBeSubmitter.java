package uk.ac.ebi.fg.annotare2.magetab.checks.idf;

import uk.ac.ebi.fg.annotare2.magetab.checker.GlobalCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static uk.ac.ebi.fg.annotare2.magetab.checks.idf.IdfConstants.SUBMITTER_ROLE;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck("At least one contact must have '" + SUBMITTER_ROLE + "' role")
public class AtLeastOneContactMustBeSubmitter implements GlobalCheck<Person> {

    private int submitterCount;

    @Override
    public void visit(Person person) {
        TermList roles = person.getRoles();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        if (roles.getNames().contains(SUBMITTER_ROLE)) {
            submitterCount++;
        }
    }

    @Override
    public void check() {
        assertThat(submitterCount, greaterThan(0));
    }
}

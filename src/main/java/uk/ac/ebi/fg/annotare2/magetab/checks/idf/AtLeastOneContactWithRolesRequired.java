package uk.ac.ebi.fg.annotare2.magetab.checks.idf;

import uk.ac.ebi.fg.annotare2.magetab.checker.GlobalCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck("At least one contact must have a role")
public class AtLeastOneContactWithRolesRequired implements GlobalCheck<Person> {

    private int roleCount;

    @Override
    public void visit(Person person) {
        TermList roles = person.getRoles();
        if (roles != null && roles.isEmpty()) {
            roleCount++;
        }
    }

    @Override
    public void check() {
        assertThat(roleCount, greaterThan(0));
    }
}

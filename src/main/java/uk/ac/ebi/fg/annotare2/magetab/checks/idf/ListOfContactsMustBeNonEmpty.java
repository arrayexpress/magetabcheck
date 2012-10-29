package uk.ac.ebi.fg.annotare2.magetab.checks.idf;

import uk.ac.ebi.fg.annotare2.magetab.checker.GlobalCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabGlobalCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Olga Melnichuk
 */
@MageTabGlobalCheck
public class ListOfContactsMustBeNonEmpty implements GlobalCheck<Person> {

    private int contactCount;

    @Override
    public void visit(Person person) {
        contactCount++;
    }

    @Override
    public void check() {
        assertThat("List of contacts must be non empty", contactCount, greaterThan(0));
    }
}

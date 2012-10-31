package uk.ac.ebi.fg.annotare2.magetab.checks.idf;

import uk.ac.ebi.fg.annotare2.magetab.checker.GlobalCheck;
import uk.ac.ebi.fg.annotare2.magetab.checker.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck("At least one contact must have an email")
public class AtLeastOneContactWithEmailRequired implements GlobalCheck<Person> {

    private int emailCount;

    @Override
    public void visit(Person person) {
        if (!isNullOrEmpty(person.getEmail())) {
            emailCount++;
        }
    }

    @Override
    public void check() {
        assertThat(emailCount, greaterThan(0));
    }
}

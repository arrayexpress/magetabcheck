package uk.ac.ebi.fg.annotare2.magetab.check;

import org.hamcrest.Matcher;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Olga Melnichuk
 */
public class IdfChecker {

    private final List<String> errors = new ArrayList<String>();

    private final List<String> warnings = new ArrayList<String>();


    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public void check(IdfData idf) {
        List<Person> contacts = idf.getContacts();

        error("List of contacts must be non empty", contacts, is(not(empty())));

        for (Person p : contacts) {
            error("Contact must have Last Name specified", p.getLastName(), not(isEmptyString()));
        }

       // error("There is must be at least one contact with email specified", contacts, hasItem(personWithEmail()));

       // error("There is must be at least one contact with roles specified ", contacts, hasItem(personWithRoles()));

       // error("There is must be at least one contact with 'submitter' role", contacts, hasItem(personWithRole("submitter")));
    }

    private <T> void error(String msg, T target, Matcher<? super T> matcher) {
        try {
            assertThat(msg, target, matcher);
        } catch (AssertionError e) {
            addError(msg);
        }
    }

    private <T> void warning(String msg, T target, Matcher<? super T> matcher) {
        try {
            assertThat(msg, target, matcher);
        } catch (AssertionError e) {
            addWarning(msg);
        }
    }

    private void addError(String err) {
        errors.add(err);
    }

    private void addWarning(String warn) {
        warnings.add(warn);
    }
}

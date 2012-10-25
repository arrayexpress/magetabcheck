package uk.ac.ebi.fg.annotare2.magetab.check;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

/**
 * @author Olga Melnichuk
 */
public class ContactMatchers {

    public static Matcher<Person> personWithEmail() {
        return new TypeSafeMatcher<Person>() {

            @Override
            protected boolean matchesSafely(Person p) {
                return not(empty()).matches(p.getEmail());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A person with non empty email");
            }
        };
    }

    public static Matcher<Person> personWithRoles() {
        return new TypeSafeMatcher<Person>() {
            @Override
            protected boolean matchesSafely(Person p) {
                TermList roles = p.getRoles();
                return roles != null &&
                        hasItem(not(empty())).matches(roles.getNames());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A person with non empty list of roles");
            }
        };
    }

    public static Matcher<Person> personWithRole(final String role) {
        return new TypeSafeMatcher<Person>() {
            @Override
            protected boolean matchesSafely(Person p) {
                TermList roles = p.getRoles();
                return roles != null &&
                        hasItem(equals(role)).matches(roles.getNames());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A person with '" + role + "' role");
            }
        };
    }
}

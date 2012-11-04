package uk.ac.ebi.fg.annotare2.magetab.checker.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Olga Melnichuk
 */
public class RegExpMatcher extends TypeSafeMatcher<String> {

    private final String regex;

    public RegExpMatcher(String regex) {
        this.regex = regex;
    }

    @Override
    protected boolean matchesSafely(String item) {
        return item.matches(regex);
    }

    public void describeTo(Description description) {
        description.appendText("matches regex='" + regex + "'");
    }

    public static RegExpMatcher matches(String regex) {
        return new RegExpMatcher(regex);
    }
}


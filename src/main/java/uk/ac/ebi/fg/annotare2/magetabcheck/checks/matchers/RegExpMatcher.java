/*
 * Copyright 2012 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.matchers;

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


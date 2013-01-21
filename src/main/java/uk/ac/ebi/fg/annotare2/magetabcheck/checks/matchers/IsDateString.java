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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class IsDateString extends TypeSafeMatcher<String> {

    private final SimpleDateFormat format;

    private IsDateString(SimpleDateFormat format) {
        this.format = format;
    }

    @Override
    public boolean matchesSafely(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            format.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void describeTo(Description description) {
        description.appendText("not a date string");
    }

    @Factory
    public static <T> Matcher<String> isDateString(SimpleDateFormat format) {
        return new IsDateString(format);
    }
}
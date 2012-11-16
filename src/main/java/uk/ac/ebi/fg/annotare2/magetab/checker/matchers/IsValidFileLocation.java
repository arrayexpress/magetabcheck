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

package uk.ac.ebi.fg.annotare2.magetab.checker.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.FileLocation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Olga Melnichuk
 */
public class IsValidFileLocation extends TypeSafeMatcher<FileLocation> {

    @Override
    protected boolean matchesSafely(FileLocation loc) {
        if (loc == null || loc.isEmpty()) {
            return false;
        }

        try {
            URL location = loc.toURL();
            if (location.getProtocol().equals("file")) {
                location.openConnection();
            }
            else {
                int response = ((HttpURLConnection) location.openConnection()).getResponseCode();
                if (response != HttpURLConnection.HTTP_OK) {
                    return false;
                }
            }
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not a valid file location");
    }

    @Factory
    public static <T> Matcher<FileLocation> isValidFileLocation() {
        return new IsValidFileLocation();
    }
}

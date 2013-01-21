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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.FileLocation;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Olga Melnichuk
 */
public class IsValidFileLocation extends TypeSafeMatcher<FileLocation> {

    private static final Logger log = LoggerFactory.getLogger(IsValidFileLocation.class);

    @Override
    protected boolean matchesSafely(FileLocation loc) {
        if (loc == null || loc.isEmpty()) {
            return false;
        }

        try {
            URL location = loc.toURL();
            String protocol = location.getProtocol();
            if ("file".equals(protocol)) {
                File file = new File(location.getFile());
                return file.exists();
            } else if ("http".equals(protocol) || "https".equals(protocol)) {
                log.debug(System.getProperty("http.proxyHost"));
                log.debug(System.getProperty("http.proxyPort"));
                log.debug(System.getProperty("https.proxyHost"));
                log.debug(System.getProperty("https.proxyPort"));
                HttpURLConnection conn = (HttpURLConnection) location.openConnection();
                int response = conn.getResponseCode();
                if (response != HttpURLConnection.HTTP_OK) {
                    conn.disconnect();
                    return false;
                }
            } else {
                log.debug("unknown protocol: {}", protocol);
                return false;
            }
        } catch (MalformedURLException e) {
            log.debug("file location validate failure: " + loc, e);
            return false;
        } catch (IOException e) {
            log.debug("file location validate failure: " + loc, e);
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

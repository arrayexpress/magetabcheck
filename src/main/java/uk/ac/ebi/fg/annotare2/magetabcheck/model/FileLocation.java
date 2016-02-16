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

package uk.ac.ebi.fg.annotare2.magetabcheck.model;

import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class FileLocation {

    private final URL context;

    private final String path;

    public FileLocation(String path) {
        this(null, path);
    }

    public FileLocation(URL context, String path) {
        this.context = context;
        this.path = path;
    }

    public boolean isEmpty() {
        return isNullOrEmpty(path);
    }

    public URL toURL() throws MalformedURLException {
        return isEmpty() ? null :
                context == null ? new URL(path) :
                        new URL(context, path);
    }

    @Override
    public String toString() {
        return "FileLocation{" +
                "context=" + context +
                ", path='" + path + '\'' +
                '}';
    }
}

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

package uk.ac.ebi.fg.annotare2.magetab.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.common.io.Closeables.closeQuietly;

/**
 * @author Olga Melnichuk
 */
public enum KnownTermSource {

    NCBI_TAXONOMY("NCBI Taxonomy", "http://www.ncbi.nlm.nih.gov/taxonomy"),

    EFO("EFO", "http://www.ebi.ac.uk/efo/"),

    ARRAY_EXPRESS("ArrayExpress", "http://www.ebi.ac.uk/arrayexpress");

    private static final Logger log = LoggerFactory.getLogger(KnownTermSource.class);

    private String name;

    private String url;

    private KnownTermSource(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public boolean equalsTo(String url) {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
            //conn.setInstanceFollowRedirects(false);
            conn.connect();
            is = conn.getInputStream();
            String originalUrl = conn.getURL().toString();
            return this.url.equalsIgnoreCase(originalUrl);
        } catch (IOException e) {
            log.info("Can't open connection for url: " + url, e);
        }  finally {
            closeQuietly(is);
        }
        return false;
    }
}

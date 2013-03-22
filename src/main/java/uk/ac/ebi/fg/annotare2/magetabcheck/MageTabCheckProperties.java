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

package uk.ac.ebi.fg.annotare2.magetabcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.services.efo.EfoServiceProperties;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class MageTabCheckProperties implements EfoServiceProperties {

    private static final Logger log = LoggerFactory.getLogger(MageTabCheckProperties.class);

    public static String CHECKER_PROPERTIES = "checker.properties";

    public static String CHECKER_DEBUG = "checker.debug";

    public static String EFO_URL = "efo.url";

    public static String EFO_CACHE_DIR = "efo.cachedir";

    public static String OWL_API_ENTITY_EXPANSION_LIMIT = "owlapi.entityExpansionLimit";

    private final Map<String, String> map = newHashMap();

    public MageTabCheckProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("properties == null");
        }
        set(CHECKER_DEBUG, properties);
        set(EFO_URL, properties);
        set(EFO_CACHE_DIR, properties);
        set(OWL_API_ENTITY_EXPANSION_LIMIT, properties);
    }

    @Override
    public URL getEfoUrl() {
        try {
            return new URL(map.get(EFO_URL));
        } catch (MalformedURLException e) {
            log.error("Malformed url for " + EFO_URL + " property");
        }
        return null;
    }

    @Override
    public File getCacheDir() {
        String dir = map.get(EFO_CACHE_DIR);
        return (dir == null ? null : new File(dir));
    }

    @Override
    public int getOwlEntityExpansionLimit() {
        String v = map.get(OWL_API_ENTITY_EXPANSION_LIMIT);
        if (v != null) {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                log.error("Not a number property value: " + OWL_API_ENTITY_EXPANSION_LIMIT);
            }
        }
        return 100000;
    }

    private void set(String propertyName, Properties properties) {
        String v = System.getProperty(propertyName);
        if (v == null) {
            v = properties.getProperty(propertyName);
        }
        map.put(propertyName, v);
    }

    public static MageTabCheckProperties load() {
        Properties defaults =
                load(MageTabCheckProperties.class.getResourceAsStream("/MageTabCheck-default.properties"), new Properties());

        Properties p = new Properties(defaults);

        String propertiesFile = System.getProperty(CHECKER_PROPERTIES);
        p = isNullOrEmpty(propertiesFile) ?
                load(MageTabCheckProperties.class.getResourceAsStream("/MageTabCheck.properties"), p) :
                load(new File(propertiesFile), p);

        return new MageTabCheckProperties(p);
    }

    private static Properties load(File file, Properties properties) {
        try {
            return load(new FileInputStream(file), properties);
        } catch (FileNotFoundException e) {
            log.error("Can't load properties from file " + file.getAbsoluteFile(), e);
        }
        return properties;
    }

    public static Properties load(InputStream in, Properties properties) {
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException e) {
                log.error("Can't load properties", e);
            }
        }
        return properties;
    }
}

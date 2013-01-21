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

package uk.ac.ebi.fg.annotare2.magetabcheck.extension;

import java.util.regex.Pattern;

/**
 * @author Olga Melnichuk
 */
public enum KnownTermSource {

    NCBI_TAXONOMY("NCBI Taxonomy", "http://www.ncbi.nlm.nih.gov/[tT]axonomy/?"),

    EFO("EFO", "http://www.ebi.ac.uk/efo/?"),

    ARRAY_EXPRESS("ArrayExpress", "http://www.ebi.ac.uk/arrayexpress"),

    MGED_ONTOLOGY("MGED Ontology", "http://mged.sourceforge.net/ontologies/MGEDontology.php");

    private String name;

    private Pattern urlPattern;

    private KnownTermSource(String name, String pattern) {
        this.name = name;
        this.urlPattern = Pattern.compile(pattern);
    }

    public boolean matches(String url) {
        return urlPattern.matcher(url).matches();
    }

    public String getName() {
        return name;
    }
}

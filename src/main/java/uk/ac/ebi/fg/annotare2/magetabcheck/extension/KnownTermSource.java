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

package uk.ac.ebi.fg.annotare2.magetabcheck.extension;

import java.util.regex.Pattern;

/**
 * @author Olga Melnichuk
 */
public enum KnownTermSource {

    ARRAY_EXPRESS("ArrayExpress",
            "The ArrayExpress Archive is a database of functional genomics experiments including gene expression " +
                    "where you can query and download data collected to MIAME and MINSEQE standards",
            "http://www.ebi.ac.uk/arrayexpress/",
            "http://www.ebi.ac.uk/arrayexpress/?"),

    NCBI_TAXONOMY("NCBI Taxonomy",
            "The Taxonomy Database is a curated classification and nomenclature for all of the organisms in the " +
                    "public sequence databases",
            "http://www.ncbi.nlm.nih.gov/taxonomy",
            "http://www.ncbi.nlm.nih.gov/[tT]axonomy/?"),

    EFO("EFO",
            "The Experimental Factor Ontology (EFO) provides a systematic description of many experimental " +
                    "variables available in EBI databases, and for external projects such as the NHGRI GWAS catalogue",
            "http://www.ebi.ac.uk/efo/",
            "http://www.ebi.ac.uk/efo/?"),

    MGED_ONTOLOGY("MGED Ontology",
            "An ontology for microarray experiments in support of MAGE v.1",
            "http://mged.sourceforge.net/ontologies/index.php",
            "http://mged.sourceforge.net/ontologies/MGEDontology.php");

    private String name;

    private String description;

    private String url;

    private Pattern urlPattern;

    private KnownTermSource(String name,
                            String description,
                            String url,
                            String pattern) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.urlPattern = Pattern.compile(pattern);
    }

    public boolean matches(String url) {
        return urlPattern.matcher(url).matches();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}

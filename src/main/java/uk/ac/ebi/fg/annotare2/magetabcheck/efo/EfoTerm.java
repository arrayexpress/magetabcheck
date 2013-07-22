/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
public class EfoTerm {

    private final String accession;
    private final String label;
    private final List<String> synonyms = newArrayList();
    private final String definition;
    private final boolean isOrganisational;

    public EfoTerm(String accession, String label, String definition, boolean isOrganisational,  Collection<String> synonyms) {
        this.accession = accession;
        this.label = label;
        this.definition = definition;
        this.isOrganisational = isOrganisational;
        this.synonyms.addAll(synonyms);
    }

    public String getAccession() {
        return accession;
    }

    public String getLabel() {
        return label;
    }

    public Collection<String> getSynonyms() {
        return unmodifiableCollection(synonyms);
    }

    public String getDefinition() {
        return definition;
    }

    public boolean isOrganisational() {
        return isOrganisational;
    }
}

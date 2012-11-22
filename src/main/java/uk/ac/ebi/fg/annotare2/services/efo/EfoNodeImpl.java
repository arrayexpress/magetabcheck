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

package uk.ac.ebi.fg.annotare2.services.efo;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
class EfoNodeImpl implements EfoNode {

    private final String id;

    private String term;

    private final List<String> alternativeTerms = newArrayList();

    private final List<EfoNode> parents = newArrayList();

    private final List<EfoNode> children = newArrayList();


    private boolean organisational;

    EfoNodeImpl(String id) {
        this.id = id;
    }

    void addAlternativeTerm(String term) {
        if (!isNullOrEmpty(term)) {
            alternativeTerms.add(term);
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public String getTerm() {
        return term;
    }

    @Override
    public Collection<String> getAlternativeTerms() {
        return unmodifiableCollection(alternativeTerms);
    }

    @Override
    public Collection<EfoNode> getParents() {
        return unmodifiableCollection(parents);
    }

    @Override
    public Collection<EfoNode> getChildren() {
        return unmodifiableCollection(children);
    }


    void setTerm(String term) {
        this.term = term;
    }

    void addChild(EfoNode child) {
        children.add(child);
    }

    void addParent(EfoNode parent) {
        parents.add(parent);
    }

    void setOrganizational(Boolean bool) {
       this.organisational = bool;
    }

    boolean isOrganisational() {
        return organisational;
    }
}

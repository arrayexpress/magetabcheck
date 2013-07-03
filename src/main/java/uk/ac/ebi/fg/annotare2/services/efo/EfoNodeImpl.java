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

    private String label;

    private String definition;

    private final List<String> alternativeTerms = newArrayList();

    private final List<EfoNodeImpl> parents = newArrayList();

    private final List<EfoNodeImpl> children = newArrayList();


    private boolean isOrganisational;

    EfoNodeImpl(String id) {
        this.id = id;
    }

    void addAlternativeTerm(String term) {
        if (!isNullOrEmpty(term)) {
            alternativeTerms.add(term);
        }
    }

    @Override
    public String getAccession() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public boolean isOrganisational() {
        return isOrganisational;
    }

    @Override
    public Collection<String> getAlternativeNames() {
        return unmodifiableCollection(alternativeTerms);
    }

    @Override
    public Collection<? extends EfoNode> getParents() {
        return unmodifiableCollection(parents);
    }

    @Override
    public Collection<? extends EfoNode> getChildren() {
        return unmodifiableCollection(children);
    }

    @Override
    public EfoTerm asTerm() {
        return new EfoTerm(getAccession(), getLabel(), getDefinition(), isOrganisational(), getAlternativeNames());
    }

    void setLabel(String label) {
        this.label = label;
    }

    void setDefinition(String definition) {
        this.definition = definition;
    }

    void addChild(EfoNodeImpl child) {
        children.add(child);
    }

    void addParent(EfoNodeImpl parent) {
        parents.add(parent);
    }

    void setOrganizational(Boolean bool) {
        this.isOrganisational = bool;
    }

    void addParents(Collection<EfoNodeImpl> moreParents) {
        parents.addAll(moreParents);
    }

    void addChildren(Collection<EfoNodeImpl> moreChildren) {
        children.addAll(moreChildren);
    }

    @Deprecated
    public boolean toBeRemoved() {
        if (!isOrganisational()) {
            return false;
        }
        for (EfoNodeImpl p : parents) {
            p.addChildren(children);
        }
        for (EfoNodeImpl ch : children) {
            ch.addParents(parents);
        }
        return true;
    }
}

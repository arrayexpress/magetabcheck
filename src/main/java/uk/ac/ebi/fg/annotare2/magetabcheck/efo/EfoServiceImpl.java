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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class EfoServiceImpl implements EfoService {

    private static final Logger log = LoggerFactory.getLogger(EfoService.class);

    private final EfoDag graph;

    public EfoServiceImpl(EfoDag graph) {
        if (graph == null) {
            throw new IllegalArgumentException("EfoGraph == null");
        }
        this.graph = graph;
    }

    @Override
    public EfoTerm findTermByLabel(final String name, String rootAccession) {
        EfoNode node = findNode(rootAccession, new Predicate<EfoNode>() {
            @Override
            public boolean apply(@Nullable EfoNode input) {
                return name.equalsIgnoreCase(input.getLabel());
            }
        });
        return node == null ? null : node.asTerm();
    }

    @Override
    public EfoTerm findTermByAccession(final String accession, String rootAccession) {
        EfoNode node = findNode(rootAccession, new Predicate<EfoNode>() {
            @Override
            public boolean apply(@Nullable EfoNode input) {
                return accession.equals(input.getAccession());
            }
        });
        return node == null ? null : node.asTerm();
    }

    @Override
    public EfoTerm findTermByLabelOrAccession(final String name, final String accession, String rootAccession) {
        if (isNullOrEmpty(accession)) {
            if (!isNullOrEmpty(name)) {
                EfoTerm term = findTermByLabel(name, rootAccession);
                if (term != null) {
                    return term;
                }
            }
        } else if (isNullOrEmpty(name)) {
            if (!isNullOrEmpty(accession)) {
                EfoTerm term = findTermByAccession(accession, rootAccession);
                if (term != null) {
                    return term;
                }
            }
        } else {
            EfoTerm term = findTermByAccession(accession, rootAccession);
            if (term != null && name.equals(term.getLabel())) {
                return term;
            }
        }
        return null;
    }

    @Override
    public EfoTerm findTermByAccession(String accession) {
        EfoNode node = graph.getNodeById(accession);
        return node == null ? null : node.asTerm();
    }

    private EfoNode findNode(String rootAccession, Predicate<EfoNode> predicate) {
        EfoNode node = graph.getNodeById(rootAccession);
        if (node == null) {
            log.error("'" + rootAccession + "' not found in EFO");
            return null;
        }
        return findDescendant(node, predicate);
    }

    private EfoNode findDescendant(EfoNode node, Predicate<EfoNode> predicate) {
        if (predicate.apply(node)) {
            return node;
        }

        for (EfoNode child : node.getChildren()) {
            EfoNode found = findDescendant(child, predicate);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}

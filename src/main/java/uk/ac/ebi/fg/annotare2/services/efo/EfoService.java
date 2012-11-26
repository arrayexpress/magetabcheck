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

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class EfoService {

    private static final Logger log = LoggerFactory.getLogger(EfoService.class);

    //TODO move EFO_ID to properties
    private static final String HTS_EXPERIMENT_TYPES = "EFO_0003740";

    private static final String MA_EXPERIMENT_TYPES = "EFO_0002696";

    private static final String LIBRARY_CONSTRUCTION_PROTOCOL = "EFO_0004184";

    private final EfoGraph graph;

    @Inject
    public EfoService(@Named("efoCachePath") String cachePath,
                      @Named("efoUrl") String efoUrl) {

        EfoGraph g = null;
        try {
            g = new EfoLoader(new File(cachePath)).load(new URL(efoUrl));
        } catch (IOException e) {
            log.error("Can't load EFO", e);
        } catch (OWLOntologyCreationException e) {
            log.error("Can't load EFO", e);
        }

        graph = g;
    }

    public EfoGraph getGraph() {
        return graph;
    }

    public EfoNode findHtsInvestigationType(String term) {
        return findByTerm(HTS_EXPERIMENT_TYPES, term);
    }

    public EfoNode findMaInvestigationType(String term) {
        return findByTerm(MA_EXPERIMENT_TYPES, term);
    }

    private EfoNode findByTerm(String startEfo, final String term) {
        EfoNode node = graph.getNodeById(startEfo);
        if (node == null) {
            log.error("EFO node class " + startEfo + "not found");
            return null;
        }
        return findDescendant(node, new Predicate<EfoNode>() {
            @Override
            public boolean apply(@Nullable EfoNode input) {
                return term.equals(input.getTerm());
            }
        });
    }

    private EfoNode findById(String startEfo, final String id) {
        EfoNode node = graph.getNodeById(startEfo);
        if (node == null) {
            log.error("EFO node class " + startEfo + "not found");
            return null;
        }
        return findDescendant(node, new Predicate<EfoNode>() {
            @Override
            public boolean apply(@Nullable EfoNode input) {
                return id.equals(input.getId());
            }
        });
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

    public boolean isLibraryConstructionProtocol(String accession, String term) {
        if (isNullOrEmpty(accession) || isNullOrEmpty(term)) {
            return false;
        }

        EfoNode node = findById(LIBRARY_CONSTRUCTION_PROTOCOL, accession);
        return node != null && term.equals(node.getTerm());

    }
}

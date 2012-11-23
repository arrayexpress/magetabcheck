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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Olga Melnichuk
 */
public class EfoService {

    private static final Logger log = LoggerFactory.getLogger(EfoService.class);

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

    //TODO move EFO_ID to properties
    public EfoNode findHtsInvestigationType(String term) {
        return findByTerm("EFO_0003740", term);
    }

    //TODO move EFO_ID to proerties
    public EfoNode findMaInvestigationType(String term) {
        return findByTerm("EFO_0002696", term);
    }

    private EfoNode findByTerm(String startEfo, String term) {
        EfoNode node = graph.getNodeById(startEfo);
        if (node == null) {
            log.error("Can't find class " + startEfo + " in EFO");
            return null;
        }
        return findDescendant(node, term);
    }

    private EfoNode findDescendant(EfoNode node, String term) {
        if (term.equals(node.getTerm())) {
            return node;
        }
        for (EfoNode child : node.getChildren()) {
            EfoNode found = findDescendant(child, term);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

}

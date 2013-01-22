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
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class EfoServiceImpl implements EfoService {

    private static final Logger log = LoggerFactory.getLogger(EfoService.class);

    private final EfoGraph graph;

    @Inject
    public EfoServiceImpl(@Named("efoCacheDir") String cacheDir,
                          @Named("efoUrl") String efoUrl) {

        EfoGraph g = null;
        try {
            File dir = (cacheDir == null ? null : new File(cacheDir));
            g = new EfoLoader(dir).load(new URL(efoUrl));
        } catch (IOException e) {
            log.error("Can't load EFO", e);
        } catch (OWLOntologyCreationException e) {
            log.error("Can't load EFO", e);
        }

        graph = g;
    }

    @Override
    public String findHtsInvestigationType(String name) {
        EfoNode node = findNodeByTermName(HTS_EXPERIMENT_TYPES, name);
        return node == null ? null : node.getAccession();
    }

    @Override
    public String findMaInvestigationType(String name) {
        EfoNode node = findNodeByTermName(MA_EXPERIMENT_TYPES, name);
        return node == null ? null : node.getAccession();
    }

    @Override
    public boolean isLibraryConstructionProtocol(String accession, String name) {
        return findByTermAccessionOrName(LIBRARY_CONSTRUCTION_PROTOCOL, accession, name) != null;
    }

    @Override
    public boolean isSequencingProtocol(String accession, String name) {
        return findByTermAccessionOrName(SEQUENCING_PROTOCOL, accession, name) != null;
    }

    @Override
    public Collection<String> getSubTermsOf(String accession) {
        List<String> list = newArrayList();
        EfoNode node = graph.getNodeById(accession);
        for (EfoNode ch : node.getChildren()) {
            list.add(ch.getName());
        }
        return list;
    }

    private EfoNode findNodeByTermName(String startEfo, final String termName) {
        return findNode(startEfo, new Predicate<EfoNode>() {
            @Override
            public boolean apply(@Nullable EfoNode input) {
                return termName.equalsIgnoreCase(input.getName());
            }
        });
    }

    private EfoNode findNodeByTermAccession(String startEfo, final String accession) {
        return findNode(startEfo, new Predicate<EfoNode>() {
            @Override
            public boolean apply(@Nullable EfoNode input) {
                return accession.equals(input.getAccession());
            }
        });
    }

    private EfoNode findByTermAccessionOrName(String startEfo, final String accession, final String name) {
        if (isNullOrEmpty(accession)) {
            if (!isNullOrEmpty(name)) {
                EfoNode node = findNodeByTermName(startEfo, name);
                if (node != null) {
                    return node;
                }
            }
        } else if (isNullOrEmpty(name)) {
            if (!isNullOrEmpty(accession)) {
                EfoNode node = findNodeByTermAccession(startEfo, accession);
                if (node != null) {
                    return node;
                }
            }
        } else {
            EfoNode node = findNodeByTermAccession(startEfo, accession);
            if (node != null && name.equals(node.getName())) {
                return node;
            }
        }
        return null;
    }

    private EfoNode findNode(String startFromId, Predicate<EfoNode> predicate) {
        EfoNode node = graph.getNodeById(startFromId);
        if (node == null) {
            log.error("EFO node class " + startFromId + "not found");
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

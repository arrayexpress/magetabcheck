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

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class EfoDag {

    private static final Logger log = LoggerFactory.getLogger(EfoDag.class);

    private final Map<String, EfoNode> efoMap;

    EfoDag(Map<String, EfoNode> efoMap) {
        this.efoMap = newHashMap(efoMap);
    }

    public EfoNode getNodeById(String efoId) {
        return efoMap.get(efoId);
    }

    public Collection<EfoNode> getRootNodes() {
        List<EfoNode> roots = newArrayList();
        for (EfoNode node : efoMap.values()) {
            if (node.getParents().isEmpty()) {
                roots.add(node);
            }
        }
        return roots;
    }

    public static class Builder {

        private final OWLOntology ontology;

        private final OWLReasoner reasoner;

        public Builder(OWLOntology ontology, OWLReasoner reasoner) {
            this.ontology = ontology;
            this.reasoner = reasoner;
        }

        public EfoDag build() {
            log.debug("Building EFO graph: loading all classes...");

            Map<String, EfoNode> efoMap = newHashMap();
            for (OWLClass cls : ontology.getClassesInSignature(true)) {
                loadClass(cls, efoMap);
            }
            log.debug("Building EFO graph: {} classes are loaded", efoMap.size());
            return new EfoDag(efoMap);
        }

        private EfoNodeImpl loadClass(OWLClass cls, Map<String, EfoNode> visited) {
            if (!reasoner.isSatisfiable(cls)) {
                return null;
            }

            String id = getId(cls);
            EfoNodeImpl node = (EfoNodeImpl) visited.get(id);
            if (node != null) {
                return node;
            }

            ClassAnnotationVisitor annotVisitor = new ClassAnnotationVisitor(id);
            for (OWLAnnotation annotation : cls.getAnnotations(ontology)) {
                annotation.accept(annotVisitor);
            }

            NodeSet<OWLClass> children = reasoner.getSubClasses(cls, true);
            EfoNodeImpl efoNode = annotVisitor.getNode();

            visited.put(id, efoNode);

            for (OWLClass childCls : children.getFlattened()) {
                EfoNodeImpl child = loadClass(childCls, visited);
                if (child != null) {
                    efoNode.addChild(child);
                    child.addParent(efoNode);
                }
            }
            return efoNode;
        }

        private String getId(OWLClass cls) {
            return cls.getIRI().toString().replaceAll("^.*?([^#/=?]+)$", "$1");
        }
    }

    public static EfoDag build(OWLOntology ontology, OWLReasoner reasoner) {
        return new Builder(ontology, reasoner).build();
    }

}

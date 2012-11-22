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

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class EfoGraph {

    private EfoVersion efoVersion;

    public EfoNode getTerm(String efoId) {
        return null;
    }

    public EfoVersion getEfoVersion() {
        return efoVersion;
    }

    private Map<String, EfoNodeImpl> efoMap = newHashMap();

    public static class Builder {

        private static Pattern ONTOLOGY_VERSION_PATTERN = Pattern.compile(".*?(\\d+(\\.\\d+)+).*");

        private static final String OBOFOUNDRY_PARTOF = "http://www.obofoundry.org/ro/ro.owl#part_of";

        private final OWLOntology ontology;

        private final OWLReasoner reasoner;

        public Builder(OWLOntology ontology, OWLReasoner reasoner) {
            this.ontology = ontology;
            this.reasoner = reasoner;
        }

        public EfoGraph build() {
            EfoGraph graph = new EfoGraph();

            graph.efoVersion = getEfoVersion();

            Map<String, EfoNodeImpl> efoMap = graph.efoMap;
            for (OWLClass cls : ontology.getClassesInSignature(true)) {
                loadClass(cls, efoMap);
            }

            eliminateOrganizationalNodes(efoMap);
            //TODO
            //addPartonomyRelations(efoMap);
            return graph;
        }

        private EfoVersion getEfoVersion() {
            String version = null;
            List<String> versionInfo = newArrayList();
            for (OWLAnnotation annotation : ontology.getAnnotations()) {
                if (!isVersionInfo(annotation)) {
                    continue;
                }

                String value = ((OWLLiteral) annotation.getValue()).getLiteral();
                if (value != null) {
                    Matcher m = ONTOLOGY_VERSION_PATTERN.matcher(value);
                    if (m.matches()) {
                        version = m.group(1);
                    }
                    versionInfo.add(value);
                }
            }
            return new EfoVersion(version, on(" ").join(versionInfo));
        }

        private boolean isVersionInfo(OWLAnnotation annotation) {
            return annotation.getValue() instanceof OWLLiteral &&
                    "versionInfo".equals(annotation.getProperty().getIRI().getFragment());
        }

        private EfoNodeImpl loadClass(OWLClass cls, Map<String, EfoNodeImpl> visited) {
            if (!reasoner.isSatisfiable(cls)) {
                return null;
            }

            String id = getId(cls);
            EfoNodeImpl node = visited.get(id);
            if (node != null) {
                return node;
            }

            ClassAnnotationVisitor annotVisitor = new ClassAnnotationVisitor(id);
            for (OWLAnnotation annotation : cls.getAnnotations(ontology)) {
                annotation.accept(annotVisitor);
            }

            NodeSet<OWLClass> children = reasoner.getSubClasses(cls, true);
            EfoNodeImpl efoNode = annotVisitor.getNode();

            for (OWLClass childCls : children.getFlattened()) {
                EfoNodeImpl child = loadClass(childCls, visited);
                if (child != null) {
                    efoNode.addChild(child);
                    child.addParent(efoNode);
                }
            }

            visited.put(id, efoNode);
            return efoNode;
        }

        private void eliminateOrganizationalNodes(Map<String, EfoNodeImpl> efoMap) {
            for(EfoNodeImpl node : efoMap.values()) {
                if (!node.isOrganisational()) {
                    continue;
                }
                //TODO
            }

        }

        private String getId(OWLClass cls) {
            return cls.getIRI().toString().replaceAll("^.*?([^#/=?]+)$", "$1");
        }

        private OWLObjectProperty getProperty(final String iri) {
            IRI propertyIRI = IRI.create(iri);

            for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature(true)) {
                if (p.getIRI().equals(propertyIRI)) {
                    return p;
                }
            }
            return null;
        }

        /* private void buildPartonomy() {
            OWLObjectProperty partOfProperty = getProperty(OBOFOUNDRY_PARTOF);
            if (partOfProperty == null) {
                return;
            }

            for (OWLClass cls : ontology.getClassesInSignature(true)) {
                Set<OWLClass> parts = getRestrictedClasses(ontology, cls, partOfProperty);

                String partId = getId(cls);
                for (OWLClass part : parts) {
                    String parentId = getId(part);

                    if (parentId.equals(partId))
                        continue;

                    EfoNode parentNode = efomap.get(parentId);
                    EfoNode node = efomap.get(partId);
                    if (parentNode != null && node != null) {
                        parentNode.children.add(node);
                        node.parents.add(parentNode);

                        log.debug("Partonomy: " + node.term + " part_of " + parentNode.term);
                    }
                }
            }

        }*/

        /* public static Set<OWLClass> getReferencedRestrictedClasses(OWLOntology ontology, OWLClass cls, OWLProperty property) {
            Set<OWLClass> classesRelatedByProperty = new HashSet<OWLClass>();

            Set<OWLSubClassOfAxiom> referencingAxioms = ontology.getSubClassAxiomsForSubClass(cls);

            Set<OWLRestriction> restrictions = filterRestrictions(referencingAxioms, property);

            for (OWLRestriction restriction : restrictions) {
                classesRelatedByProperty.addAll(restriction.getClassesInSignature());
            }
            return classesRelatedByProperty;
        }


        private static Set<OWLRestriction> filterRestrictions(Set<OWLSubClassOfAxiom> axioms, OWLProperty property) {
            Set<OWLRestriction> restrictionsOfInterest = new HashSet<OWLRestriction>();
            for (OWLSubClassOfAxiom axiom : axioms) {

                //get all the superclasses of this axiom
                OWLClassExpression parentClass = axiom.getSuperClass();

                //if the axiom is of type OWLRestriction
                if (parentClass instanceof OWLRestriction) {

                    //cast the axiom to type OWLRestriction (done inline)
                    OWLRestriction restriction = (OWLRestriction) parentClass;

                    // get all restrictions that restrict the supplied property
                    if (restriction.getProperty().getObjectPropertiesInSignature().contains(property) ||
                            restriction.getProperty().getDataPropertiesInSignature().contains(property)) {
                        //add to restrictions
                        restrictionsOfInterest.add((OWLRestriction) parentClass);
                    }
                }
            }

            return restrictionsOfInterest;
        }*/
    }


    public static EfoGraph build(OWLOntology ontology, OWLReasoner reasoner) {
        return new Builder(ontology, reasoner).build();
    }

}

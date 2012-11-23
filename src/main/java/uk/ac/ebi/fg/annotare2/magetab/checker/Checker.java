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

package uk.ac.ebi.fg.annotare2.magetab.checker;

import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetab.model.Identity;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.*;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.HasAttributes;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraph;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraphAttribute;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraphNode;

import java.util.*;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.magetab.checker.AllChecks.getCheckRunnersFor;

/**
 * @author Olga Melnichuk
 */
public class Checker {

    private static final Logger log = LoggerFactory.getLogger(Checker.class);

    private final InvestigationType invType;

    private List<CheckResult> results = new ArrayList<CheckResult>();

    private final Injector injector;

    public Checker(Injector injector, InvestigationType type) {
        this.injector = injector;
        this.invType = type;
    }

    public void check(IdfData idf) {
        checkOne(idf.getInfo());
        checkAll(idf.getContacts(), Person.class);
        checkAll(idf.getExperimentDesigns(), ExperimentalDesign.class);
        checkAll(idf.getExperimentalFactors(), ExperimentalFactor.class);
        checkAll(idf.getQualityControlTypes(), QualityControlType.class);
        checkAll(idf.getReplicateTypes(), ReplicateType.class);
        checkAll(idf.getNormalizationTypes(), NormalizationType.class);
        checkAll(idf.getProtocols(), Protocol.class);
        checkAll(idf.getPublications(), Publication.class);
        checkAll(idf.getTermSources(), TermSource.class);
    }

    public void check(SdrfGraph graph) {
        //TODO do we still need context?
        Map<Class<?>, Object> context = newHashMap();

        Set<Identity> marks = newHashSet();
        Queue<SdrfGraphNode> queue = newArrayDeque();
        queue.addAll(graph.getRootNodes());
        while (!queue.isEmpty()) {
            SdrfGraphNode node = queue.poll();
            Identity id = new Identity(node);
            if (marks.contains(id)) {
                continue;
            }
            checkNode(node, context);
            marks.add(id);
            for (SdrfGraphNode n : node.getChildNodes()) {
                queue.add(n);
            }
        }
    }

    private void checkNode(SdrfGraphNode node, Map<Class<?>, Object> context) {
        checkOne(node, context);
        checkAttributes(node, context);
    }

    private void checkAttributes(HasAttributes obj, Map<Class<?>, Object> context) {
        for (SdrfGraphAttribute attr : obj.getAttributes()) {
            checkOne(attr, context);
            checkAttributes(attr, context);
        }
    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) {
        List<CheckRunner<T>> checkRunners = getCheckRunnersFor(itemClass, invType);
        if (checkRunners.isEmpty()) {
            return;
        }
        for (T item : collection) {
            runAllWith(checkRunners, item, Collections.<Class<?>, Object>emptyMap());
        }
        sumUp(checkRunners);
    }

    private <T> void checkOne(T item) {
        checkOne(item, Collections.<Class<?>, Object>emptyMap());
    }

    @SuppressWarnings("unchecked")
    private <T> void checkOne(T item, Map<Class<?>, Object> context) {
        List<CheckRunner<T>> checkRunners = getCheckRunnersFor((Class<T>) item.getClass(), invType);
        if (checkRunners.isEmpty()) {
            return;
        }
        runAllWith(checkRunners, item, context);
        sumUp(checkRunners);
    }

    private <T> void runAllWith(List<CheckRunner<T>> checkRunners, T item, Map<Class<?>, Object> context) {
        for (CheckRunner<T> runner : checkRunners) {
            runner.runWith(item, context);
        }
    }

    private <T> void sumUp(List<CheckRunner<T>> checkRunners) {
        for (CheckRunner<T> runner : checkRunners) {
            results.addAll(runner.sumUp());
        }
    }

    public Collection<CheckResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}

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

package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.Experiment;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Olga Melnichuk
 */
public class Checker {

    private final ExperimentType invType;

    private List<CheckResult> results = newArrayList();

    private final List<CheckDefinition> allChecks = newArrayList();

    @Inject
    public Checker(List<CheckDefinition> allChecks, @Assisted ExperimentType type) {
        this.invType = type;
        this.allChecks.addAll(allChecks);
    }

    public Collection<CheckResult> check(Experiment exp) {
        check(exp.getIdfData());
        check(exp.getSdrfGraph());
        return getResults();
    }

    private void check(IdfData idf) {
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

    private void check(SdrfGraph graph) {
        //TODO do we still need a context?
        Map<Class<?>, Object> context = newHashMap();

        Set<SdrfGraphEntity> marks = newHashSet();
        Queue<SdrfGraphNode> queue = newArrayDeque();
        queue.addAll(graph.getRootNodes());
        while (!queue.isEmpty()) {
            SdrfGraphNode node = queue.poll();
            if (marks.contains(node)) {
                continue;
            }
            checkOne(node, context);
            checkAttributes(node, context, marks);
            marks.add(node);
            for (SdrfGraphNode n : node.getChildNodes()) {
                queue.add(n);
            }
        }
    }

    private void checkAttributes(HasAttributes obj, Map<Class<?>, Object> context, Set<SdrfGraphEntity> marks) {
        for (SdrfGraphAttribute attr : obj.getAttributes()) {
            if (marks.contains(attr)) {
                continue;
            }
            checkOne(attr, context);
            marks.add(attr);
            checkAttributes(attr, context, marks);
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

    public <T> List<CheckRunner<T>> getCheckRunnersFor(Class<T> itemClass, ExperimentType invType) {
        List<CheckRunner<T>> runners = newArrayList();

        for (CheckDefinition def : allChecks) {
            if (def.isApplicable(itemClass, invType)) {
                runners.add(def.newRunner(itemClass));
            }
        }
        return runners;
    }


    public Collection<CheckResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}

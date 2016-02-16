/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.Experiment;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;
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

    private final List<CheckDefinition> allChecks = newArrayList();

    private List<CheckRunWatcher> watchers;

    @Inject
    public Checker(List<CheckDefinition> allChecks, @Assisted ExperimentType type) {
        this.invType = type;
        this.allChecks.addAll(allChecks);
    }

    public Collection<CheckResult> check(Experiment exp) {
        watchers = newArrayList();
        for (CheckDefinition cd : allChecks) {
            watchers.add(new CheckRunWatcher(cd));
        }
        check(exp.getIdfData());
        check(exp.getSdrfGraph());
        return summarize();
    }

    private void check(IdfData idf) {
        if (idf == null) {
            return;
        }
        checkOne(idf.getInfo());
        checkAll(idf.getComments());
        checkAll(idf.getContacts());
        checkAll(idf.getExperimentDesigns());
        checkAll(idf.getExperimentalFactors());
        checkAll(idf.getQualityControlTypes());
        checkAll(idf.getReplicateTypes());
        checkAll(idf.getNormalizationTypes());
        checkAll(idf.getProtocols());
        checkAll(idf.getPublications());
        checkAll(idf.getTermSources());
    }

    private void check(SdrfGraph graph) {
        if (graph == null) {
            return;
        }
        Set<SdrfGraphEntity> visited = newHashSet();
        Queue<SdrfGraphNode> queue = newArrayDeque();
        queue.addAll(graph.getRootNodes());
        while (!queue.isEmpty()) {
            SdrfGraphNode node = queue.poll();
            if (visited.contains(node)) {
                continue;
            }
            checkOne(node);
            checkAttributes(node, visited);
            visited.add(node);
            for (SdrfGraphNode n : node.getChildNodes()) {
                queue.add(n);
            }
        }
    }

    private void checkAttributes(HasAttributes obj, Set<SdrfGraphEntity> marks) {
        for (SdrfGraphAttribute attr : obj.getAttributes()) {
            if (marks.contains(attr)) {
                continue;
            }
            checkOne(attr);
            marks.add(attr);
            checkAttributes(attr, marks);
        }
    }

    private <T> void checkAll(Collection<T> collection) {
        for (T item : collection) {
            checkOne(item);
        }
    }

    private <T> void checkOne(T item) {
        for (CheckRunWatcher watcher : watchers) {
            watcher.run(item, invType);
        }
    }

    private List<CheckResult> summarize() {
        List<CheckResult> results = newArrayList();
        for (CheckRunWatcher watcher : watchers) {
            results.addAll(watcher.summarize());
        }
        return results;
    }

    private static class CheckRunWatcher {

        private final CheckDefinition def;
        private Map<Class<?>, CheckRunner<?>> runners;
        private Map<Class<?>, Object> targets;
        private final List<CheckResult> results;

        private CheckRunWatcher(CheckDefinition def) {
            this.def = def;
            results = newArrayList();
            targets = newHashMap();
            runners = newHashMap();
        }

        @SuppressWarnings("unchecked")
        public <T> void run(T item, ExperimentType invType) {
            if (!def.isApplicable(item.getClass(), invType)) {
                return;
            }

            CheckRunner<T> runner;
            Object target;
            if (targets.containsKey(def.getCheckClass())) {
                target = targets.get(def.getCheckClass());
            } else {
                target = def.getCheckInstance();
                targets.put(def.getCheckClass(), target);
            }

            if (def.getType().isClassBased()) {
                if (runners.containsKey(item.getClass())) {
                    runner = ((CheckRunner<T>) runners.get(item.getClass()));
                } else {
                    runner = (CheckRunner<T>) def.newRunner(item.getClass(), target);
                    runners.put(item.getClass(), runner);
                }
            } else {
                runner = (CheckRunner<T>) def.newRunner(item.getClass(), target);
            }
            runner.runWith(item, Maps.<Class<?>, Object>newHashMap());
            if (!def.getType().isClassBased()) {
                sumUp(runner.sumUp());
            }
        }

        public List<CheckResult> summarize() {
            if (def.getType().isClassBased()) {
                for(CheckRunner<?> runner : runners.values()) {
                    sumUp(runner.sumUp());
                }
            }
            return results;
        }

        private void sumUp(List<CheckResult> checkResults) {
            results.addAll(checkResults);
        }
    }
}

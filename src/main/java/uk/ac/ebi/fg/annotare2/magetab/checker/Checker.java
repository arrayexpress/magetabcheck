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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.*;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static uk.ac.ebi.fg.annotare2.magetab.checker.AllChecks.checkRunnersFor;

/**
 * @author Olga Melnichuk
 */
public class Checker {

    private final InvestigationType invType;

    private List<CheckResult> results = new ArrayList<CheckResult>();

    public Checker(InvestigationType invType) {
        this.invType = invType;
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

    public void check(SDRF sdrf, IdfData idf) {
        Set<Object> context = new HashSet<Object>();
        context.add(sdrf.getLayout());
        context.add(idf);

        Set<SDRFNode> marks = newHashSet();
        Queue<SDRFNode> queue = new ArrayDeque<SDRFNode>();
        queue.addAll(sdrf.getRootNodes());
        while (!queue.isEmpty()) {
            SDRFNode node = queue.poll();
            if (marks.contains(node)) {
                continue;
            }
            checkOne(node, context);
            marks.add(node);
            for (Node n : node.getChildNodes()) {
                if (n instanceof SDRFNode) {
                    queue.add((SDRFNode) n);
                }
            }
        }
    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) {
        List<CheckRunner<T>> checkRunners = checkRunnersFor(itemClass, invType);
        if (checkRunners.isEmpty()) {
            return;
        }
        for (T item : collection) {
            runAllWith(checkRunners, item, emptySet());
        }
        sumUp(checkRunners);
    }

    private <T> void checkOne(T item) {
        checkOne(item, emptySet());
    }

    @SuppressWarnings("unchecked")
    private <T> void checkOne(T item, Set<Object> context) {
        List<CheckRunner<T>> checkRunners = checkRunnersFor((Class<T>) item.getClass(), invType);
        if (checkRunners.isEmpty()) {
            return;
        }
        runAllWith(checkRunners, item, context);
        sumUp(checkRunners);
    }

    private <T> void runAllWith(List<CheckRunner<T>> checkRunners, T item, Set<Object> context) {
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

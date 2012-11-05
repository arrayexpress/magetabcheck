/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

import uk.ac.ebi.fg.annotare2.magetab.model.idf.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        checkAll(idf.getPublications(), Publication.class);
    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) {
        List<CheckRunner<T>> checkRunners = checkRunnersFor(itemClass, invType);
        if (checkRunners.isEmpty()) {
            return;
        }
        for (T item : collection) {
            runAllWith(checkRunners, item);
        }
        sumUp(checkRunners);
    }

    @SuppressWarnings("unchecked")
    private <T> void checkOne(T item) {
        List<CheckRunner<T>> checkRunners = checkRunnersFor((Class<T>) item.getClass(), invType);
        if (checkRunners.isEmpty()) {
            return;
        }
        runAllWith(checkRunners, item);
        sumUp(checkRunners);
    }

    private <T> void runAllWith(List<CheckRunner<T>> checkRunners, T item) {
        for (CheckRunner<T> runner : checkRunners) {
            runner.runWith(item);
        }
    }

    private <T> void sumUp(List<CheckRunner<T>> checkRunners) {
        for (CheckRunner<T> runner : checkRunners) {
            results.addAll(runner.sumUp());
        }
    }

    public List<CheckResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}

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

import com.google.inject.Inject;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class AllChecks {

    private final List<CheckDefinition> allChecks = newArrayList();

    @Inject
    public AllChecks(List<CheckDefinition> checks) {
        allChecks.addAll(checks);
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
}

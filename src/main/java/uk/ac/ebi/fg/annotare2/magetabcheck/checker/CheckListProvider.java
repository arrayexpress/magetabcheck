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
import com.google.inject.Injector;
import com.google.inject.Provider;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class CheckListProvider implements Provider<List<CheckDefinition>> {

    private final List<CheckDefinition> checks = newArrayList();

    @Inject
    public CheckListProvider(final Injector injector, AllChecks allChecks) {
        ClassInstanceProvider instanceProvider = new ClassInstanceProvider() {
            @Override
            public <T> T newInstance(Class<T> clazz) {
                return injector.getInstance(clazz);
            }
        };
        for (final Class<?> clazz : allChecks.getClassBasedChecks()) {
            checks.add(new ClassBasedCheckDefinition(clazz, instanceProvider));
        }
        for (Method method : allChecks.getMethodBasedChecks()) {
            checks.add(new MethodBasedCheckDefinition(method, instanceProvider));
        }
    }

    @Override
    public List<CheckDefinition> get() {
        return checks;
    }
}

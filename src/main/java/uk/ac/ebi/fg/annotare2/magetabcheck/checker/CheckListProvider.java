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
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class CheckListProvider implements Provider<List<CheckDefinition>> {

    private final List<CheckDefinition> checks = newArrayList();

    private final InstanceProvider instanceProvider;

    @Inject
    public CheckListProvider(final Injector injector) {

        instanceProvider = new InstanceProvider() {
            @Override
            public <T> T newInstance(Class<T> clazz) {
                return injector.getInstance(clazz);
            }
        };

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath())
                        .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner())
        );

        Set<Class<?>> classBasedChecks = reflections.getTypesAnnotatedWith(MageTabCheck.class);
        for (final Class<?> clazz : classBasedChecks) {
            checks.add(new ClassBasedCheckDefinition(clazz, instanceProvider));
        }

        Set<Method> methodBasedChecks = reflections.getMethodsAnnotatedWith(MageTabCheck.class);
        for (Method method : methodBasedChecks) {
            checks.add(new MethodBasedCheckDefinition(method, instanceProvider));
        }
    }

    @Override
    public List<CheckDefinition> get() {
        return checks;
    }

}

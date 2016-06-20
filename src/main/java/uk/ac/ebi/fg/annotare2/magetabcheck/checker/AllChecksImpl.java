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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.Method;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Olga Melnichuk
 */
public class AllChecksImpl implements AllChecks {

    private final Set<Class<?>> classBasedChecks = newHashSet();
    private final Set<Method> methodBasedChecks = newHashSet();

    AllChecksImpl() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath())
                        .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner())
        );
        classBasedChecks.addAll(reflections.getTypesAnnotatedWith(MageTabCheck.class));
        methodBasedChecks.addAll(reflections.getMethodsAnnotatedWith(MageTabCheck.class));
    }

    @Override
    public Set<Class<?>> getClassBasedChecks() {
        return classBasedChecks;
    }

    @Override
    public Set<Method> getMethodBasedChecks() {
        return methodBasedChecks;
    }
}

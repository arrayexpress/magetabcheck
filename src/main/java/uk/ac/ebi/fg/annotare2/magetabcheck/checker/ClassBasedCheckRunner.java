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

import com.google.inject.ConfigurationException;
import com.google.inject.ProvisionException;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPositionKeeper.getCheckPosition;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckRunner<T> extends AbstractCheckRunner<T> {

    private final ClassBasedCheckDefinition classDef;

    private Object target;

    @SuppressWarnings("unchecked")
    ClassBasedCheckRunner(ClassBasedCheckDefinition classDef) {
        super(isNotNull(classDef.getAnnotation()));

        this.classDef = classDef;

        try {
            target = classDef.getInstance();
        } catch (ConfigurationException e) {
            error(e);
        } catch (ProvisionException e) {
            error(e);
        }
    }

    private static MageTabCheck isNotNull(MageTabCheck annotation) {
        if (annotation == null) {
            throw new NullPointerException(
                    "Global MageTab check class must be annotated with MageTabCheck annotation");
        }
        return annotation;
    }

    @Override
    public void runWith(T item, Map<Class<?>, Object> context) {
        try {
            classDef.invokeSetContext(target, context);
            classDef.invokeVisit(target, item);
        } catch (IllegalAccessException e) {
            error(e);
        } catch (InvocationTargetException e) {
            error(e.getCause());
        }
    }

    @Override
    public List<CheckResult> sumUp() {
        if (!hasErrors()) {
            try {
                classDef.invokeCheck(target);
                success();
            } catch (AssertionError e) {
                failure();
            } catch (IllegalAccessException e) {
                error(e);
            } catch (InvocationTargetException e) {
                Throwable t = e.getCause();
                if (t instanceof AssertionError) {
                    failure(getCheckPosition());
                } else {
                    error(t);
                }
            }
        }
        return super.sumUp();
    }
}

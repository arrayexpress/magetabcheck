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

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckRunner<T> extends AbstractCheckRunner<T> {

    private final GlobalCheck<T> target;

    private final Method contextSetter;

    ClassBasedCheckRunner(Injector injector, Class<? extends GlobalCheck<T>> targetClass) {
        super(isNotNull(targetClass.getAnnotation(MageTabCheck.class)));

        Method contextSetter = null;
        for(Method m : targetClass.getMethods()) {
            if (m.getAnnotation(CheckContext.class) != null) {
                contextSetter = m;
                break;
            }
        }
        this.contextSetter = contextSetter;

        GlobalCheck<T> target = null;
        try {
            target = injector.getInstance(targetClass);
        } catch (ConfigurationException e) {
            error(e);
        } catch (ProvisionException e) {
            error(e);
        }

        this.target = target;
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
            if (contextSetter != null) {
                contextSetter.invoke(target, getParams(contextSetter, context));
            }
            target.visit(item);
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
                target.check();
                success();
            } catch (AssertionError e) {
                failure();
            }
        }
        return super.sumUp();
    }
}

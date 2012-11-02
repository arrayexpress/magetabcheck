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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckContext.clearContext;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckContext.getCheckPosition;

/**
 * @author Olga Melnichuk
 */
class MethodBasedCheckRunner<T> extends CheckRunner<T> {

    private final Class<?> clazz;

    private final Method method;

    MethodBasedCheckRunner(Class<?> clazz, Method method) {
        super(isNotNull(method.getAnnotation(MageTabCheck.class)));
        this.clazz = clazz;
        this.method = method;
    }

    private static MageTabCheck isNotNull(MageTabCheck annotation) {
        if (annotation == null) {
            throw new NullPointerException("Method based MageTab check must be annotated with MageTabCheck annotation");
        }
        return annotation;
    }

    @Override
    public void runWith(T item) {
        clearContext();
        try {
            method.invoke(clazz.newInstance(), item);
            success();
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof AssertionError) {
                failure(getCheckPosition());
            } else {
                error(t);
            }
        } catch (InstantiationException e) {
            error(e);
        } catch (IllegalAccessException e) {
            error(e);
        }
    }
}

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckPositionKeeper.clearCheckPosition;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckPositionKeeper.getCheckPosition;

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

    private static Map<Class<?>, Object> add(Map<Class<?>, Object> map, Object item) {
        Map<Class<?>, Object> newMap = newHashMap(map);
        newMap.put(item.getClass(), item);
        return newMap;
    }

    @Override
    public void runWith(T item, Map<Class<?>, Object> context) {
        clearCheckPosition();
        try {
            Object[] params = getParams(method, add(context, item));
            method.invoke(clazz.newInstance(), params);
            success(getCheckPosition());
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

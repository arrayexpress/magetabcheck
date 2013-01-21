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

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Context;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckDefinition extends CheckDefinition {

    private static final Logger log = LoggerFactory.getLogger(ClassBasedCheckDefinition.class);

    private final Class<?> clazz;

    private final InstanceProvider instanceProvider;

    protected ClassBasedCheckDefinition(Class<?> clazz, InstanceProvider instanceProvider) {
        super(clazz.getAnnotation(MageTabCheck.class), getSubjectTypes(clazz));
        this.clazz = clazz;
        this.instanceProvider = instanceProvider;
    }

    @Override
    public <T> CheckRunner<T> newRunner(Class<T> itemClass) {
        return new ClassBasedCheckRunner<T>(this);
    }

    public Object getInstance() {
        return instanceProvider.newInstance(clazz);
    }

    public void invokeSetContext(Object target, Map<Class<?>, Object> context) throws IllegalAccessException, InvocationTargetException {
        Method setContext = getMethodMarkedAsContext();
        if (setContext != null) {
            setContext.invoke(target, getParams(setContext, context));
        }
    }

    public void invokeVisit(Object target, Object subject) throws InvocationTargetException, IllegalAccessException {
        Method visit = getMethodMarkedAsVisit(subject.getClass());
        if (visit != null) {
            visit.invoke(target, subject);
        }
    }

    public void invokeCheck(Object target) throws InvocationTargetException, IllegalAccessException {
        Method check = getMethodMarkedAsCheck();
        if (check != null) {
            check.invoke(target);
        }
    }

    final Method getMethodMarkedAsCheck() {
        Collection<Method> methods = findMethods(new Predicate<Method>() {
            @Override
            public boolean apply(@Nullable Method m) {
                return m != null
                        && m.getAnnotation(Check.class) != null
                        && m.getParameterTypes().length == 0;
            }
        });
        return methods.isEmpty() ? null : methods.iterator().next();
    }

    final Method getMethodMarkedAsVisit(final Class<?> subjectType) {
        Collection<Method> methods = findMethods(new Predicate<Method>() {
            @Override
            public boolean apply(@Nullable Method m) {
                return m != null
                        && m.getAnnotation(Visit.class) != null
                        && m.getParameterTypes().length == 1
                        && m.getParameterTypes()[0].isAssignableFrom(subjectType);
            }
        });
        return methods.isEmpty() ? null : methods.iterator().next();
    }

    final Method getMethodMarkedAsContext() {
        Collection<Method> methods = findMethods(new Predicate<Method>() {
            @Override
            public boolean apply(@Nullable Method m) {
                return m != null
                        && m.getAnnotation(Context.class) != null
                        && m.getParameterTypes().length == 0;
            }
        });
        return methods.isEmpty() ? null : methods.iterator().next();
    }

    private Collection<Method> findMethods(Predicate<Method> predicate) {
        return findMethods(clazz, predicate);
    }

    private static Collection<Class> getSubjectTypes(Class<?> clazz) {
        List<Class> list = newArrayList();

        for (Method m : findMethods(clazz, new Predicate<Method>() {
            @Override
            public boolean apply(@Nullable Method m) {
                return m != null
                        && m.getAnnotation(Visit.class) != null
                        && m.getParameterTypes().length == 1;
            }
        })) {
            list.add(m.getParameterTypes()[0]);
        }
        return list;
    }

    private static Collection<Method> findMethods(Class<?> clazz, Predicate<Method> predicate) {
        return filter(Arrays.asList(clazz.getMethods()), predicate);
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable type.
     *
     * @param type the type
     * @return the underlying class
     */
/*
    protected static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else {
            return null;
        }
    }
*/

    /**
     * Returns {@link ParameterizedType} for {@link GlobalCheck} interface if the given class implements it;
     * otherwise <code>null</code>
     *
     * @param clazz the class to check
     * @return a {@link ParameterizedType} for {@link GlobalCheck} interface or <code>null</code>
     */
/*
    protected static ParameterizedType getGlobalCheckInterface(Class<?> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type interf : interfaces) {
            Type rawType = ((ParameterizedType) interf).getRawType();
            if (rawType instanceof Class &&
                    rawType.equals(GlobalCheck.class)) {
                return (ParameterizedType) interf;
            }
        }
        return null;
    }
*/

    /**
     * Get the actual type arguments a child class has used to extend a generic base class.
     *
     * @param clazz the class to check
     * @return a list of the raw classes for the actual type arguments.
     */
/*
    protected static Class<?> getGlobalCheckTypeArgument(Class<?> clazz) {
        Type type = clazz;
        ParameterizedType interf = null;

        while (interf == null && type != null && !getClass(type).equals(Object.class)) {
            if (type instanceof Class) {
                interf = getGlobalCheckInterface((Class) type);
                if (interf == null) {
                    type = ((Class) type).getGenericSuperclass();
                } else {
                    return getClass(interf.getActualTypeArguments()[0]);
                }
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                interf = getGlobalCheckInterface(rawType);
                if (interf == null) {
                    type = ((Class) rawType).getGenericSuperclass();
                } else {

                    String typeVarName = ((Class) interf.getRawType()).getTypeParameters()[0].getName();
                    Type t = interf.getActualTypeArguments()[0];
                    if (t instanceof Class) {
                        return getClass(t);
                    }

                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        if (typeParameters[i].getName().equals(typeVarName)) {
                            return getClass(actualTypeArguments[i]);
                        }
                    }
                }
            }
        }
        return null;
    }
*/
}

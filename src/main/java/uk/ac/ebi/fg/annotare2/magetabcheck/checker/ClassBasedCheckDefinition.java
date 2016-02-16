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

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Context;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckDefinition extends CheckDefinition {

    private final Class<?> clazz;

    private final ClassInstanceProvider instanceProvider;

    private final Method setContext;

    private final Method check;

    private final Map<Class, Method> visitMethods = newHashMap();

    protected ClassBasedCheckDefinition(Class<?> clazz, ClassInstanceProvider instanceProvider) {
        super(clazz.getAnnotation(MageTabCheck.class));
        this.clazz = clazz;
        this.instanceProvider = instanceProvider;

        setContext = getMethodMarkedAsContext();
        check = getMethodMarkedAsCheck();
        Collection<Method> allVisitMethods = getMethodsMarkedAsVisit();
        for (Method m : allVisitMethods) {
            Class<?> paramType = null;
            Type type = m.getGenericParameterTypes()[0];
            if (type instanceof TypeVariable) {
                TypeVariable typeVar = (TypeVariable) type;
                Class<?> superClass = m.getDeclaringClass();
                TypeVariable[] vars = ((Class) superClass).getTypeParameters();
                int index = -1;
                for (int i = 0; i < vars.length; i++) {
                    if (vars[i].getName().equals(typeVar.getName())) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    Class[] paramTypes = findActualTypes(clazz, superClass);
                    if (index < paramTypes.length) {
                        paramType = paramTypes[index];
                    }
                }
            } else if (type instanceof Class) {
                paramType = (Class) type;
            }
            if (paramType != null) {
                visitMethods.put(paramType, m);
            }
        }
    }

    @Override
    public <T> CheckRunner<T> newRunner(Class<T> itemClass, Object target) {
        return new ClassBasedCheckRunner<T>(this, target);
    }

    @Override
    protected boolean isSubjectTypeAssignableFrom(Class objType) {
        for (Class<?> type : visitMethods.keySet()) {
            if (type.isAssignableFrom(objType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CheckType getType() {
        return CheckType.CLASS_BASED;
    }

    @Override
    public Class<?> getCheckClass() {
        return clazz;
    }

    public Object getCheckInstance() {
        return instanceProvider.newInstance(clazz);
    }

    public void invokeSetContext(Object target, Map<Class<?>, Object> context) throws IllegalAccessException, InvocationTargetException {
        if (setContext != null) {
            setContext.invoke(target, getParams(setContext, context));
        }
    }

    public void invokeCheck(Object target) throws InvocationTargetException, IllegalAccessException {
        if (check != null) {
            check.invoke(target);
        }
    }

    public void invokeVisit(Object target, Object subject) throws InvocationTargetException, IllegalAccessException {
        Method visit = getMethodMarkedAsVisit(subject.getClass());
        if (visit != null) {
            visit.invoke(target, subject);
        }
    }

    final Method getMethodMarkedAsVisit(Class<?> subjectType) {
        Method visit = null;
        for (Class<?> paramType : visitMethods.keySet()) {
            if (paramType.isAssignableFrom(subjectType)) {
                visit = visitMethods.get(paramType);
            }
        }
        return visit;
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

    final Collection<Method> getMethodsMarkedAsVisit() {
        return findMethods(new Predicate<Method>() {
            @Override
            public boolean apply(@Nullable Method m) {
                return (m != null)
                        && (m.getAnnotation(Visit.class) != null)
                        && m.getParameterTypes().length == 1;
            }
        });
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

    private static Collection<Method> findMethods(Class<?> clazz, Predicate<Method> predicate) {
        return filter(Arrays.asList(clazz.getMethods()), predicate);
    }

    protected static Class[] findActualTypes(Class<?> base, Class<?> superClass) {
        Class[] actuals = new Class[0];
        for (Class clazz = base; !clazz.equals(superClass); clazz = clazz.getSuperclass()) {
            if (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
                continue;
            }

            Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            Class[] nextActuals = new Class[types.length];
            for (int i = 0; i < types.length; i++) {
                if (types[i] instanceof Class) {
                    nextActuals[i] = (Class) types[i];
                } else {
                    nextActuals[i] = map(clazz.getTypeParameters(), types[i], actuals);
                }
            }
            actuals = nextActuals;
        }
        return actuals;
    }

    protected static Class map(Object[] variables, Object variable, Class[] actuals) {
        for (int i = 0; i < variables.length && i < actuals.length; i++) {
            if (variables[i].equals(variable)) {
                return actuals[i];
            }
        }
        return null;
    }
}

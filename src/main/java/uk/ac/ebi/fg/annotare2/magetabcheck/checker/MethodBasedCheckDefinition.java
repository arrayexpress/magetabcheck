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

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
class MethodBasedCheckDefinition extends CheckDefinition {

    private final Method method;

    private final ClassInstanceProvider instanceProvider;

    public MethodBasedCheckDefinition(Method method, ClassInstanceProvider instanceProvider) {
        super(method.getAnnotation(MageTabCheck.class));
        this.method = method;
        this.instanceProvider = instanceProvider;
    }

    @Override
    public <T> CheckRunner<T> newRunner(Class<T> itemClass) {
        return new MethodBasedCheckRunner<T>(this);
    }

    @Override
    protected boolean isSubjectTypeAssignableFrom(Class objType) {
        Class<?> subjectType = getFirstParameter(method);
        return subjectType != null && subjectType.isAssignableFrom(objType);
    }

    private static Class<?> getFirstParameter(Method method) {
        Class[] types = method.getParameterTypes();
        return types == null || types.length == 0 ? null : types[0];
    }

    public void invoke(Map<Class<?>, Object> context) throws InvocationTargetException, IllegalAccessException {
        Object[] params = getParams(method, context);
        method.invoke(instanceProvider.newInstance(method.getDeclaringClass()), params);
    }
}

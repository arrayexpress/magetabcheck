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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */

public abstract class CheckDefinition {

    private final MageTabCheck annotation;

    protected CheckDefinition(MageTabCheck annotation) {
        this.annotation = annotation;
    }

    public MageTabCheck getAnnotation() {
        return annotation;
    }

    public boolean isApplicable(Class<?> objType, ExperimentType expType) {
        return isAnnotApplicableTo(expType) && isSubjectTypeAssignableFrom(objType);
    }

    private boolean isAnnotApplicableTo(ExperimentType expType) {
        return annotation != null && annotation.application().appliesTo(expType);
    }

    protected static Object[] getParams(Method method, Map<Class<?>, Object> context) throws IllegalAccessException {
        Class<?>[] types = method.getParameterTypes();
        List<Object> params = newArrayList();
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            for (Class<?> keyType : context.keySet()) {
                if (type.isAssignableFrom(keyType)) {
                    params.add(context.get(keyType));
                    break;
                }
            }
            if (params.size() != i + 1) {
                throw new IllegalAccessException("Can't find object of class " + type + " in the check context");
            }
        }
        return params.toArray(new Object[params.size()]);
    }

    public abstract <T> CheckRunner<T> newRunner(Class<T> itemClass);

    protected abstract boolean isSubjectTypeAssignableFrom(Class objType);
}

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

import com.google.inject.ConfigurationException;
import com.google.inject.ProvisionException;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDynamicDetailSetter.clearCheckDynamicDetail;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDynamicDetailSetter.getCheckDynamicDetail;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPositionSetter.clearCheckPosition;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPositionSetter.getCheckPosition;

/**
 * @author Olga Melnichuk
 */
class MethodBasedCheckRunner<T> extends AbstractCheckRunner<T> {

    private final MethodBasedCheckDefinition methodDef;
    private final Object target;

    MethodBasedCheckRunner(MethodBasedCheckDefinition def, Object target) {
        super(isNotNull(def.getAnnotation()));
        this.methodDef = def;
        this.target = target;
    }

    private static MageTabCheck isNotNull(MageTabCheck annotation) {
        if (annotation == null) {
            throw new NullPointerException("Method-based MageTab check must be annotated with MageTabCheck annotation");
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
        clearCheckDynamicDetail();
        try {
            methodDef.invoke(target, add(context, item));
            success(getCheckPosition());
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof AssertionError) {
                failure(getCheckPosition(), getCheckDynamicDetail());
            } else {
                error(t);
            }
        } catch (IllegalAccessException e) {
            error(e);
        } catch (ConfigurationException e) {
            error(e);
        } catch (ProvisionException e) {
            error(e);
        }
    }
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkBroken;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkFailed;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkSucceeded;

/**
 * @author Olga Melnichuk
 */
abstract class CheckRunner<T> {

    private static final Logger log = LoggerFactory.getLogger(CheckRunner.class);

    private List<CheckResult> results = newArrayList();

    private String checkTitle;

    private CheckModality checkModality;

    private boolean hasErrors = false;

    protected CheckRunner(String checkTitle, CheckModality checkModality) {
        this.checkTitle = checkTitle;
        this.checkModality = checkModality;
    }

    protected CheckRunner(@Nonnull MageTabCheck annot) {
        this(annot.value(), annot.modality());
    }

    protected void success() {
        success(null);
    }

    protected void success(CheckPosition pos) {
        results.add(checkSucceeded(checkTitle, checkModality, pos));
    }

    protected void failure() {
        failure(null);
    }

    protected void failure(CheckPosition pos) {
        results.add(checkFailed(checkTitle, checkModality, pos));
    }

    protected void error(Throwable e) {
        log.error("Check running error(" + checkTitle + ")", e);
        results.add(checkBroken(checkTitle, checkModality, e));
        hasErrors = true;
    }

    protected boolean hasErrors() {
        return hasErrors;
    }

    protected static Object[] getParams(Method method, Set<Object> context) throws IllegalAccessException {
        Class<?>[] types = method.getParameterTypes();
        List<Object> params = newArrayList();
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            for (Object obj : context) {
                if (type.isAssignableFrom(obj.getClass())) {
                    params.add(obj);
                    break;
                }
            }
            if (params.size() != i + 1) {
                throw new IllegalAccessException("Can't find object of class " + type + " in the check context");
            }
        }
        return params.toArray(new Object[params.size()]);
    }

    public List<CheckResult> sumUp() {
        return Collections.unmodifiableList(results);
    }

    public abstract void runWith(T item, Set<Object> context);
}

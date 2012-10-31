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

import java.util.List;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckRunner<T> extends CheckRunner<T> {

    private final GlobalCheck<T> target;

    ClassBasedCheckRunner(Class<? extends GlobalCheck<T>> targetClass) {
        super(isNotNull(targetClass.getAnnotation(MageTabCheck.class)));

        GlobalCheck<T> t = null;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException e) {
            error(e);
        } catch (IllegalAccessException e) {
            error(e);
        }

        this.target = t;
    }

    private static MageTabCheck isNotNull(MageTabCheck annotation) {
        if (annotation == null) {
            throw new NullPointerException(
                    "Global MageTab check class must be annotated with MageTabCheck annotation");
        }
        return annotation;
    }

    @Override
    public void runForEach(T item) {
        target.visit(item);
    }

    @Override
    public List<CheckResult> sumUp() {
        try {
            target.check();
            success();
        } catch (AssertionError e) {
            failure(e);
        }
        return super.sumUp();
    }
}

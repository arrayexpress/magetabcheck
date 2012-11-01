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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkBroken;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkFailed;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkSucceeded;

/**
 * @author Olga Melnichuk
 */
abstract class CheckRunner<T> {

    private List<CheckResult> results = newArrayList();

    private String checkTitle;

    private CheckModality checkModality;

    protected CheckRunner(String checkTitle, CheckModality checkModality) {
        this.checkTitle = checkTitle;
        this.checkModality = checkModality;
    }

    protected CheckRunner(@Nonnull MageTabCheck annot) {
        this(annot.value(), annot.modality());
    }

    protected void success() {
        results.add(checkSucceeded(checkTitle));
    }

    protected void failure(AssertionError assertionError) {
        results.add(checkFailed(checkTitle, checkModality, assertionError.getMessage()));
    }

    protected void error(Exception e) {
        results.add(checkBroken(checkTitle, checkModality, e));
    }

    public List<CheckResult> sumUp() {
        return Collections.unmodifiableList(results);
    }

    public abstract void runWith(T item);
}

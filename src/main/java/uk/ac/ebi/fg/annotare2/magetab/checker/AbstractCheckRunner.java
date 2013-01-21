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
import uk.ac.ebi.fg.annotare2.magetab.checker.annotation.MageTabCheck;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.*;

/**
 * @author Olga Melnichuk
 */
abstract class AbstractCheckRunner<T> implements CheckRunner<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCheckRunner.class);

    private List<CheckResult> results = newArrayList();

    private String checkTitle;

    private CheckModality checkModality;

    private boolean hasErrors = false;

    protected AbstractCheckRunner(String checkTitle, CheckModality checkModality) {
        this.checkTitle = checkTitle;
        this.checkModality = checkModality;
    }

    protected AbstractCheckRunner(@Nonnull MageTabCheck annot) {
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

    public List<CheckResult> sumUp() {
        return Collections.unmodifiableList(results);
    }

    public abstract void runWith(T item, Map<Class<?>, Object> context);
}

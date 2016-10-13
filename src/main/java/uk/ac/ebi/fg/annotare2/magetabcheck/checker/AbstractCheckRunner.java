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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult.*;

/**
 * @author Olga Melnichuk
 */
abstract class AbstractCheckRunner<T> implements CheckRunner<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCheckRunner.class);

    private List<CheckResult> results = newArrayList();

    private String checkTitle;

    private CheckModality checkModality;

    private  String checkReference;

    private boolean hasErrors = false;

    protected AbstractCheckRunner(String checkTitle, CheckModality checkModality, String checkReference) {
        this.checkTitle = checkTitle;
        this.checkModality = checkModality;
        this.checkReference = checkReference;
    }

    protected AbstractCheckRunner(@Nonnull MageTabCheck annot) {
        this(annot.value(), annot.modality(), annot.ref());
    }

    protected void success() {
        success(null);
    }

    protected void success(CheckPosition pos) {
        results.add(checkSucceeded(checkTitle, checkModality, pos, checkReference));
    }

    protected void failure() {
        failure(null, null);
    }

    protected void failure(CheckPosition pos, String dynamicDetail) {
        results.add(checkFailed(checkTitle, checkModality, pos, dynamicDetail, checkReference));
    }

    protected void error(Throwable e) {
        log.error("Check running error(" + checkTitle + ")", e);
        results.add(checkBroken(checkTitle, checkModality, e, checkReference));
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

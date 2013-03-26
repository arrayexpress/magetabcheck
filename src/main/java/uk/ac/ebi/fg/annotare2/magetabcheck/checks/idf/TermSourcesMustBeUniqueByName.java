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

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf;

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;

import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "TS02",
        value = "Name of a term source must be unique")
public class TermSourcesMustBeUniqueByName {

    private final Set<String> names = newHashSet();

    private boolean hasDuplicates = false;

    @Visit
    public void visit(TermSource termSource) {
        String name = termSource.getName().getValue();
        if (isNullOrEmpty(name) || hasDuplicates) {
            return;
        }
        hasDuplicates = names.contains(name);
    }

    @Check
    public void check() {
        assertThat(hasDuplicates, is(Boolean.FALSE));
    }
}

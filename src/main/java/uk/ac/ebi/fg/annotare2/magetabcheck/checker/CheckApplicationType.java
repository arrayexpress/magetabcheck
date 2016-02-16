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

import java.util.EnumSet;

import static java.util.EnumSet.allOf;
import static java.util.EnumSet.of;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType.HTS;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType.MICRO_ARRAY;

/**
 * @author Olga Melnichuk
 */
public enum CheckApplicationType {
    ANY(allOf(ExperimentType.class)),
    HTS_ONLY(of(HTS)),
    MICRO_ARRAY_ONLY(of(MICRO_ARRAY));

    private final EnumSet<ExperimentType> enumSet;

    private CheckApplicationType(EnumSet<ExperimentType> enumSet) {
        this.enumSet = enumSet;
    }

    public boolean appliesTo(ExperimentType type) {
        return enumSet.contains(type);
    }
}

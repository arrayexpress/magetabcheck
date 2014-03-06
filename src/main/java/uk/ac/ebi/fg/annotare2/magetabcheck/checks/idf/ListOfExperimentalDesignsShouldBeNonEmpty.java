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

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckModality;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.NonEmptyRangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ExperimentalDesign;

import javax.annotation.Nullable;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "ED01",
        value = "Experiment must have at least one experimental design specified",
  application = CheckApplicationType.MICRO_ARRAY_ONLY,
  modality = CheckModality.WARNING
     )
public class ListOfExperimentalDesignsShouldBeNonEmpty extends NonEmptyRangeCheck<ExperimentalDesign> {
    public ListOfExperimentalDesignsShouldBeNonEmpty() {
        super(new Predicate<ExperimentalDesign>() {
            @Override
            public boolean apply(@Nullable ExperimentalDesign design) {
                return design.getName() != null &&
                        design.getName().getValue() != null;
            }
        });
    }
}

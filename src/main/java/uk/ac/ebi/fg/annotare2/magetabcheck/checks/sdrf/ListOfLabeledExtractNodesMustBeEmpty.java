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

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.EmptyListCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfLabeledExtractNode;

import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.HTS_ONLY;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "LE01",
        value = "There are must not be any Labeled Extract nodes in HTS experiments",
        application = HTS_ONLY)
public class ListOfLabeledExtractNodesMustBeEmpty extends EmptyListCheck<SdrfLabeledExtractNode>{
}

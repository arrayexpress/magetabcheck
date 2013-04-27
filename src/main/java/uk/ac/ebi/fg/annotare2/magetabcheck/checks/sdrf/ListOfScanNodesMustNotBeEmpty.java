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
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.NonEmptyRangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfScanNode;

import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.HTS_ONLY;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "SC02",
        value = "An SDRF graph must have at least one scan node",
        application = HTS_ONLY)
public class ListOfScanNodesMustNotBeEmpty extends NonEmptyRangeCheck<SdrfScanNode> {
}

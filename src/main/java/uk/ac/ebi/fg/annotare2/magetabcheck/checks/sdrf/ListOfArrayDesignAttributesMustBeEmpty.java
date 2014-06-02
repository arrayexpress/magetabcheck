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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.ArrayDesignAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.EmptyRangeCheck;

import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.HTS_ONLY;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "AD04",
        value = "There must not be any array design attributes in a sequencing experiment",
        application = HTS_ONLY)
public class ListOfArrayDesignAttributesMustBeEmpty extends EmptyRangeCheck<ArrayDesignAttribute> {
}

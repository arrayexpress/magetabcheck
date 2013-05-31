/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Term;

/**
 * @author Olga Melnichuk
 */
public interface MageTabCheckEfo {

    public static final String AE_EXPERIMENT_TYPES = "EFO_0004120";

    public static final String ARRAY_ASSAY = "EFO_0002696";

    public static final String HTS_ASSAY = "EFO_0002697";

    public static final String HTS_EXPERIMENT_TYPES = "EFO_0003740";

    public static final String MA_EXPERIMENT_TYPES = "EFO_0002696";

    public static final String LIBRARY_CONSTRUCTION_PROTOCOL = "EFO_0004184";

    public static final String SEQUENCING_PROTOCOL = "EFO_0004170";

    public static final String BIOLOGICAL_VARIATION_DESINGS = "EFO_0004667";

    public static final String METHODOLOGICAL_VARIATION_DESIGNS = "EFO_0004669";

    public static final String BIOMOLECULAR_ANNOTATION_DESIGNS = "EFO_0004665";

    /**
     * Looks through the all descendants of {@value #HTS_EXPERIMENT_TYPES} term and returns
     * accession of the term which name equals to the given one.
     *
     * @param name name of the term to find
     * @return term accession or <code>null</code> if term was not found
     */
    String findHtsInvestigationType(String name);

    /**
     * Looks through the all descendants of {@value #MA_EXPERIMENT_TYPES} term and returns
     * accession of the term which name equals to the given one.
     *
     * @param name name of the term to find
     * @return term accession or <code>null</code> if term was not found
     */
    String findArrayInvestigationType(String name);

    /**
     * Checks if the given term accession and name correspond to the existed EFO term located in the
     * {@value #LIBRARY_CONSTRUCTION_PROTOCOL} branch. At least on of arguments (accession or name) should be not null.
     *
     * @param term a term from IDF to check
     * @return <code>true</code> if
     */
    boolean isLibraryConstructionProtocol(Term term);

    /**
     * Checks if the given term accession and name correspond to the existed EFO term located in the
     * {@value #LIBRARY_CONSTRUCTION_PROTOCOL} branch. At least on of arguments (accession or name) should be not null.
     *
     * @param term a term from IDF to check
     * @return <code>true</code> if
     */
    boolean isSequencingProtocol(Term term);
}

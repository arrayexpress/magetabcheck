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

package uk.ac.ebi.fg.annotare2.magetabcheck.model.idf;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface Protocol {

    Cell<String> getName();

    Cell<String> getDescription();

    Cell<List<String>> getParameters();

    Cell<String> getHardware();

    Cell<String> getSoftware();

    Cell<String> getContact();

    ProtocolType getType();

}

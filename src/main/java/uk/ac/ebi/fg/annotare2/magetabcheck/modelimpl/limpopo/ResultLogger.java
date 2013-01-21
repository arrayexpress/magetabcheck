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

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olga Melnichuk
 */
abstract class ResultLogger {

    private static final Logger log = LoggerFactory.getLogger(ResultLogger.class);

    public static void logResult(String str) {
        log.info(str);
    }
}

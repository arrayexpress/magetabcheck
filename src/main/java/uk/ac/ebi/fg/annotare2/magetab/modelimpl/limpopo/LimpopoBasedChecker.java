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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetab.checker.Checker;
import uk.ac.ebi.fg.annotare2.magetab.checker.InvestigationType;

import java.io.File;
import java.util.List;

import static java.lang.System.exit;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedChecker {

    private static final Logger log = LoggerFactory.getLogger(LimpopoBasedChecker.class);

    public static void main(String... args) {
        if (args.length == 0) {
            log.info("Usage: LimpopoBasedParser <IDF file path>");
            exit(0);
        }

        MAGETABParser parser = new MAGETABParser();
        try {
            MAGETABInvestigation inv = parser.parse(new File(args[0]));
            // TODO need to know investigation type somehow...
            Checker ch = new Checker(InvestigationType.MICRO_ARRAY);
            ch.check(new LimpopoIdfDataProxy(inv.IDF));
            List<CheckResult> results = ch.getResults();
            int success = 0, errors = 0, warnings = 0, exceptions = 0, other = 0;
            for (CheckResult res : results) {
                log.info(res.asString());
                if (res.isSuccess()) {
                    success++;
                } else if (res.isWarning()) {
                    warnings++;
                } else if (res.isError()) {
                    errors++;
                } else if (res.isException()) {
                    exceptions++;
                } else {
                    other++;
                }
            }
            log.info("---");
            log.info("total=[" + results.size() + "]" +
                    ", successes=[" + success + "]" +
                    ", errors=[" + errors + "]" +
                    ", warnings=[" + warnings + "]" +
                    ", exceptions=[" + exceptions + "]" +
                    ", other=[" + other + "]");
        } catch (ParseException e) {
            log.error("Can not parse file", e);
        }
    }
}

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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.fg.annotare2.magetab.CheckerModule;
import uk.ac.ebi.fg.annotare2.magetab.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetab.UndefinedInvestigationTypeException;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResultStatus;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.idf.LimpopoIdfDataProxy;
import uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.sdrf.LimpopoBasedSdrfGraph;

import java.io.File;
import java.util.Collection;

import static com.google.common.collect.Ordering.natural;
import static java.lang.System.exit;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedChecker {

    private static final Logger log = LoggerFactory.getLogger(LimpopoBasedChecker.class);

    public static void main(String... args) {
        if (args.length == 0) {
            log.info("Usage: LimpopoBasedChecker /path/to/idf");
            exit(0);
        }

        Injector injector = Guice.createInjector(new CheckerModule());
        MageTabChecker checker = new MageTabChecker(injector);

        MAGETABParser parser = new MAGETABParser();
        try {
            MAGETABInvestigation inv = parser.parse(new File(args[0]));
            IdfData idf = new LimpopoIdfDataProxy(inv.IDF);
            Collection<CheckResult> results = checker.check(idf, new LimpopoBasedSdrfGraph(inv.SDRF, idf));
            results = natural().sortedCopy(results);

            int success = 0, errors = 0, warnings = 0, exceptions = 0;
            for (CheckResult res : results) {
                log.info(res.asString());
                CheckResultStatus status = res.getStatus();
                switch (status) {
                    case SUCCESS:
                        success++;
                        break;
                    case WARNING:
                        warnings++;
                        break;
                    case ERROR:
                        errors++;
                        break;
                    case EXCEPTION:
                        exceptions++;
                }
            }
            log.info("---");
            log.info("total=[" + results.size() + "]" +
                    ", successes=[" + success + "]" +
                    ", errors=[" + errors + "]" +
                    ", warnings=[" + warnings + "]" +
                    ", exceptions=[" + exceptions + "]");
        } catch (ParseException e) {
            log.error("MAGE-TAB parse error", e);
        } catch (UndefinedInvestigationTypeException e) {
            log.error("Can't run checker without knowing the experiment type", e);
        }
    }
}

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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.fg.annotare2.magetabcheck.CheckerModule;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResultStatus;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UnknownExperimentTypeException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static com.google.common.collect.Ordering.natural;
import static java.lang.System.exit;
import static uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.ResultLogger.logResult;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedChecker {

    private static final Logger log = LoggerFactory.getLogger(LimpopoBasedChecker.class);

    private final MageTabChecker checker;

    private MAGETABParser parser;

    public LimpopoBasedChecker() {
        this(new CheckerModule());
    }

    public LimpopoBasedChecker(Module module) {
        Injector injector = Guice.createInjector(module);
        checker = injector.getInstance(MageTabChecker.class);
        parser = new MAGETABParser();
    }

    void runAll(String... idfPaths) {
        for (String idfPath : idfPaths) {
            run(idfPath);
        }
    }

    void run(String idfPath) {
        try {
            logResult("/* ");
            logResult(" * Running checker for: " + idfPath);
            logResult(" *\\");

            Collection<CheckResult> results = check(idfPath);
            int failures = 0, warnings = 0, errors = 0;
            for (CheckResult res : results) {
                CheckResultStatus status = res.getStatus();
                switch (status) {
                    case WARNING:
                        warnings++;
                        break;
                    case FAILURE:
                        failures++;
                        break;
                    case ERROR:
                        errors++;
                }
                if (status != CheckResultStatus.SUCCESS) {
                    logResult(res.asString());
                }
            }
            logResult("");
            logResult("total=[" + results.size() + "]" +
                    ", failures=[" + failures + "]" +
                    ", warnings=[" + warnings + "]" +
                    ", errors=[" + errors + "]");
            logResult("---");
        } catch (ParseException e) {
            log.error("MAGE-TAB parse error", e);
        } catch (UnknownExperimentTypeException e) {
            log.error("Can't run checker without knowing the experiment type", e);
        } catch (MalformedURLException e) {
            log.error("Can't create an URL", e);
        }
    }

    /**
     * Run checker on IDF file path.
     *
     * @param idfFilePath path to IDF file
     * @return a collection of check results
     * @throws MalformedURLException         if given string is not a file path
     * @throws ParseException                if IDF can not be parsed by Limpopo MAGE-TAB parser
     * @throws uk.ac.ebi.fg.annotare2.magetabcheck.checker.UnknownExperimentTypeException if IDF doesn't contain Comment[AEExperimentType] or its value is not from EFO
     */
    public final Collection<CheckResult> check(String idfFilePath) throws MalformedURLException, ParseException, UnknownExperimentTypeException {
        return check(parse(idfFilePath));
    }

    /**
     * Run checker on MAGETABInvestigation object (already parsed by Limpopo MAGE-TAB parser).
     *
     * @param inv an MAGETABInvestigation object
     * @return a collection of check results
     * @throws uk.ac.ebi.fg.annotare2.magetabcheck.checker.UnknownExperimentTypeException if MAGETABInvestigation.IDF doesn't contain Comment[AEExperimentType] or its value is not from EFO
     */
    public final Collection<CheckResult> check(MAGETABInvestigation inv) throws UnknownExperimentTypeException {
        return natural().sortedCopy(checker.check(new LimpopoBasedExperiment(inv)));
    }

    private MAGETABInvestigation parse(String idfPath) throws ParseException, MalformedURLException {
        if (idfPath.startsWith("http")) {
            return parser.parse(new URL(idfPath));
        }
        return parser.parse(new File(idfPath));
    }

    public static void main(String... args) {
        if (args.length == 0) {
            log.info("Usage: LimpopoBasedChecker /path/to/idf1 /path/to/idf2 ...");
            exit(1);
        }

        setLogLevel();

        (new LimpopoBasedChecker()).runAll(args);
    }

    private static void setLogLevel() {
        String debug = System.getProperty("checker.debug");
        if (debug != null && Boolean.parseBoolean(debug)) {
            org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
        }
    }
}

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetab.checker.Checker;

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
            Checker ch = new Checker();
            ch.check(new LimpopoIdfDataProxy(inv.IDF));
            List<CheckResult> results = ch.getResults();
            log.info("Results: " + results.size());
            for(CheckResult res : results) {
                log.info("" + res);
            }
        } catch (ParseException e) {
            log.error("Can not parse file", e);
        }
    }
}

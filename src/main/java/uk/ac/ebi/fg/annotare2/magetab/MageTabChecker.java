package uk.ac.ebi.fg.annotare2.magetab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResultStatus;
import uk.ac.ebi.fg.annotare2.magetab.checker.Checker;
import uk.ac.ebi.fg.annotare2.magetab.checker.InvestigationType;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraph;

import java.util.List;

import static com.google.common.collect.Ordering.natural;

/**
 * @author Olga Melnichuk
 */
public class MageTabChecker {

    private static final Logger log = LoggerFactory.getLogger(MageTabChecker.class);

    public void check(IdfData idf, SdrfGraph sdrf) {
        // TODO need to know investigation type somehow...
        Checker ch = new Checker(InvestigationType.MICRO_ARRAY);
        ch.check(idf);
        ch.check(sdrf);
        List<CheckResult> results = natural().sortedCopy(ch.getResults());

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

    }
}

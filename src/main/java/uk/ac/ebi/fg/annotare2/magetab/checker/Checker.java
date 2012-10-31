package uk.ac.ebi.fg.annotare2.magetab.checker;

import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class Checker {

    private final InvestigationType invType = InvestigationType.MICRO_ARRAY;

    private List<CheckResult> results = new ArrayList<CheckResult>();

    public void check(IdfData idf) {
        checkAll(idf.getContacts(), Person.class);
    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) {
        List<CheckRunner<T>> checkRunners = AllChecks.<T>checkRunnersFor(itemClass);
        if (checkRunners.isEmpty()) {
            return;
        }
        for (T item : collection) {
            for (CheckRunner<T> runner : checkRunners) {
                runner.runForEach(item);
            }
        }
        for (CheckRunner<T> runner : checkRunners) {
            results.addAll(runner.sumUp());
        }
    }
}

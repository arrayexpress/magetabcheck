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
        //TODO
        checkAll(new ArrayList<Person>(), Person.class);
    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) {
        CheckRunnerList<T> checkRunnerList = AllChecks.<T>checkRunnersFor(itemClass);
        if (checkRunnerList.isEmpty()) {
            return;
        }
        for(T item : collection) {
            checkRunnerList.visit(item);
        }
        checkRunnerList.check();
        results.addAll(checkRunnerList.getResults());
    }

    public static void main(String... args) {
        new Checker().check(null);
    }
}

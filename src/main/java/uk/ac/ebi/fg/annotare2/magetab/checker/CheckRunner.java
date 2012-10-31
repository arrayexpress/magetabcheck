package uk.ac.ebi.fg.annotare2.magetab.checker;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkBroken;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkFailed;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult.checkSucceeded;

/**
 * @author Olga Melnichuk
 */
abstract class CheckRunner<T> {

    private List<CheckResult> results = newArrayList();

    private String checkTitle;

    private CheckModality checkModality;

    protected CheckRunner(String checkTitle, CheckModality checkModality) {
        this.checkTitle = checkTitle;
        this.checkModality = checkModality;
    }

    protected CheckRunner(@Nonnull MageTabCheck annot) {
        this(annot.value(), annot.modality());
    }

    protected void success() {
        results.add(checkSucceeded(checkTitle));
    }

    protected void failure(AssertionError assertionError) {
        results.add(checkFailed(checkTitle, checkModality, assertionError.getMessage()));
    }

    protected void error(Exception e) {
        results.add(checkBroken(checkTitle, checkModality, e));
    }

    public List<CheckResult> sumUp() {
        return Collections.unmodifiableList(results);
    }

    public abstract void runForEach(T item);
}

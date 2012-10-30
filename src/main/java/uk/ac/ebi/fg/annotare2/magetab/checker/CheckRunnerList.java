package uk.ac.ebi.fg.annotare2.magetab.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
class CheckRunnerList<T> {

    private final List<CheckResult> results = new ArrayList<CheckResult>();

    private final List<MethodBasedCheckRunner<T>> methodBasedRunners = new ArrayList<MethodBasedCheckRunner<T>>();

    private final List<ClassBasedCheckRunner<T>> classBasedRunners = new ArrayList<ClassBasedCheckRunner<T>>();

    CheckRunnerList(List<MethodBasedCheckRunner<T>> methodBasedRunners, List<ClassBasedCheckRunner<T>> classBasedRunners) {
        this.methodBasedRunners.addAll(methodBasedRunners);
        this.classBasedRunners.addAll(classBasedRunners);
    }

    void visit(T obj) {
        for (MethodBasedCheckRunner<T> runner : methodBasedRunners) {
            results.add(runner.check(obj));
        }
        for (ClassBasedCheckRunner<T> runner : classBasedRunners) {
            runner.visit(obj);
        }
    }

    void check() {
        for (ClassBasedCheckRunner<T> checker : classBasedRunners) {
            results.add(checker.check());
        }
    }

    public boolean isEmpty() {
        return true;
    }

    public List<CheckResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}

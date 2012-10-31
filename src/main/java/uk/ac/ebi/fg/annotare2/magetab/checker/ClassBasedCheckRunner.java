package uk.ac.ebi.fg.annotare2.magetab.checker;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckRunner<T> extends CheckRunner<T> {

    private final GlobalCheck<T> target;

    ClassBasedCheckRunner(Class<? extends GlobalCheck<T>> targetClass) {
        super(isNotNull(targetClass.getAnnotation(MageTabGlobalCheck.class)));

        GlobalCheck<T> t = null;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException e) {
            error(e);
        } catch (IllegalAccessException e) {
            error(e);
        }

        this.target = t;
    }

    private static MageTabGlobalCheck isNotNull(MageTabGlobalCheck annotation) {
        if (annotation == null) {
            throw new NullPointerException(
                    "Global MageTab check class must be annotated with MageTabGlobalCheck annotation");
        }
        return annotation;
    }

    @Override
    public void runForEach(T item) {
        target.visit(item);
    }

    @Override
    public List<CheckResult> sumUp() {
        try {
            target.check();
            success();
        } catch (AssertionError e) {
            failure(e);
        }
        return super.sumUp();
    }
}

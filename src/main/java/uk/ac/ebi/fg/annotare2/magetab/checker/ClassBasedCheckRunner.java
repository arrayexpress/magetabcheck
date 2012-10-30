package uk.ac.ebi.fg.annotare2.magetab.checker;

/**
 * @author Olga Melnichuk
 */
class ClassBasedCheckRunner<T> {

    private final Class targetClass;

    private final GlobalCheck<T> target;

    private Throwable exception;

    ClassBasedCheckRunner(Class<? extends GlobalCheck<T>> targetClass) {

        GlobalCheck<T> t = null;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException e) {
            this.exception = e;
        } catch (IllegalAccessException e) {
            this.exception = e;
        }

        this.target = t;
        this.targetClass = targetClass;
    }

    public void visit(T obj) {
        target.visit(obj);
    }

    public CheckResult check() {
        try {
            target.check();
        } catch (AssertionError e) {
            //TODO
        }
        return null;
    }
}

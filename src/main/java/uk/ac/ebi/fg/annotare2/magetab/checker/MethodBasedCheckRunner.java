package uk.ac.ebi.fg.annotare2.magetab.checker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Olga Melnichuk
 */
class MethodBasedCheckRunner<T> {

    private final Class<?> clazz;

    private final Method method;

    MethodBasedCheckRunner(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public CheckResult check(T t) {
        try {
            method.invoke(clazz.newInstance(), t);
        } catch (AssertionError assertionError) {
            //TODO
        } catch (InvocationTargetException e) {
            //TODO
        } catch (InstantiationException e) {
            //TODO
        } catch (IllegalAccessException e) {
            //TODO
        }
        return null;
    }

}

package uk.ac.ebi.fg.annotare2.magetab.checker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Olga Melnichuk
 */
class MethodBasedCheckRunner<T> extends CheckRunner<T> {

    private final Class<?> clazz;

    private final Method method;

    MethodBasedCheckRunner(Class<?> clazz, Method method) {
        super(isNotNull(method.getAnnotation(MageTabCheck.class)));
        this.clazz = clazz;
        this.method = method;
    }

    private static MageTabCheck isNotNull(MageTabCheck annotation) {
        if (annotation == null) {
            throw new NullPointerException("Method based MageTab check must be annotated with MageTabCheck annotation");
        }
        return annotation;
    }

    @Override
    public void runForEach(T item) {
        try {
            method.invoke(clazz.newInstance(), item);
            success();
        } catch (AssertionError assertionError) {
            failure(assertionError);
        } catch (InvocationTargetException e) {
            error(e);
        } catch (InstantiationException e) {
            error(e);
        } catch (IllegalAccessException e) {
            error(e);
        }
    }
}

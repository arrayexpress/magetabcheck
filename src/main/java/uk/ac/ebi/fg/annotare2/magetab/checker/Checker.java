package uk.ac.ebi.fg.annotare2.magetab.checker;

import uk.ac.ebi.fg.annotare2.magetab.checks.idf.*;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class Checker {

    private final List<Class> methodBasedChecks = new ArrayList<Class>() {
        {
            add(IdfSimpleChecks.class);
        }
    };

    private final List<Class> classBasedChecks = new ArrayList<Class>() {
        {
            add(AtLeastOneContactMustBeSubmitter.class);
            add(AtLeastOneContactWithEmailRequired.class);
            add(AtLeastOneContactWithRolesRequired.class);
            add(AtLeastOneSubmitterMustHaveEmail.class);
            add(ListOfContactsMustBeNonEmpty.class);
        }
    };

    private final InvestigationType invType = InvestigationType.MICRO_ARRAY;

    public void check(IdfData idf) {
        //checkAll(new ArrayList<Person>(), Person.class);

    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<ClassBasedChecker<T>> cbCheckers = new ArrayList<ClassBasedChecker<T>>();
        for (Class clazz : classBasedChecks) {
            Class typeArg = getTypeArgument(clazz);
            if (typeArg != null && (typeArg.equals(itemClass))) {
                cbCheckers.add(new ClassBasedChecker<T>(clazz));
            }
        }

        List<MethodBasedChecker<T>> mbCheckers = new ArrayList<MethodBasedChecker<T>>();
        for (Class clazz : methodBasedChecks) {
            for (Method method : clazz.getMethods()) {
                MageTabCheck annot = method.getAnnotation(MageTabCheck.class);
                if (annot != null) {
                    mbCheckers.add(new MethodBasedChecker<T>(clazz, method));
                }
            }
        }

       /* for (T item : collection) {
            for (MethodBasedChecker<T> checker : mbCheckers) {
                results.add(checker.check(item));
            }
            for (ClassBasedChecker<T> checker : cbCheckers) {
                checker.visit(item);
            }
        }

        for (ClassBasedChecker<T> checker : cbCheckers) {
            results.add(checker.check());
        }  */
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable type.
     *
     * @param type the type
     * @return the underlying class
     */
    protected static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else {
            return null;
        }
    }

    protected static ParameterizedType getGlobalCheckInterface(Class<?> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type interf : interfaces) {
            Type rawType = ((ParameterizedType) interf).getRawType();
            if (rawType instanceof Class &&
                    rawType.equals(GlobalCheck.class)) {
                return (ParameterizedType)interf;
            }
        }
        return null;
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic base class.
     *
     * @param clazz the class to check
     * @return a list of the raw classes for the actual type arguments.
     */
    protected static Class<?> getTypeArgument(Class<?> clazz) {
        Type type = clazz;
        ParameterizedType interf = null;

        while (interf == null && type != null && !getClass(type).equals(Object.class)) {
            if (type instanceof Class) {
                interf = getGlobalCheckInterface((Class) type);
                if (interf == null) {
                    type = ((Class) type).getGenericSuperclass();
                } else {
                    return getClass(interf.getActualTypeArguments()[0]);
                }
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                interf = getGlobalCheckInterface(rawType);
                if (interf == null) {
                    type = ((Class) rawType).getGenericSuperclass();
                } else {

                    String typeVarName = ((Class)interf.getRawType()).getTypeParameters()[0].getName();
                    Type t = interf.getActualTypeArguments()[0];
                    if (t instanceof Class) {
                        return getClass(t);
                    }

                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        if (typeParameters[i].getName().equals(typeVarName)) {
                            return getClass(actualTypeArguments[i]);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void main(String... args) {
        new Checker().check(null);
    }

    private static class MethodBasedChecker<T> {

        private final Class clazz;

        private final Method method;

        private final MageTabCheck annot;

        private MethodBasedChecker(Class clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
            this.annot = method.getAnnotation(MageTabCheck.class);
        }

       /* public CheckResult check(T t) throws IllegalAccessException, InstantiationException, InvocationTargetException {
            try {
                method.invoke(clazz.newInstance(), t);
            } catch (AssertionError assertionError) {
                //TODO
            }
            return null;
        }   */

    }

    private static class ClassBasedChecker<T> {

        private final Class clazz;

        private final GlobalCheck<T> target;

        private ClassBasedChecker(Class<? extends GlobalCheck<T>> clazz) throws IllegalAccessException, InstantiationException {
            this.clazz = clazz;
            this.target = clazz.newInstance();
        }


    }
}

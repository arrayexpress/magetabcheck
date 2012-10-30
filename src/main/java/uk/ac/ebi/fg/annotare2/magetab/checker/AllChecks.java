package uk.ac.ebi.fg.annotare2.magetab.checker;

import uk.ac.ebi.fg.annotare2.magetab.checks.idf.*;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AllChecks {

    private static final List<Class> methodBasedChecks = new ArrayList<Class>() {
        {
            add(IdfSimpleChecks.class);
        }
    };

    private static final List<Class> classBasedChecks = new ArrayList<Class>() {
        {
            add(AtLeastOneContactMustBeSubmitter.class);
            add(AtLeastOneContactWithEmailRequired.class);
            add(AtLeastOneContactWithRolesRequired.class);
            add(AtLeastOneSubmitterMustHaveEmail.class);
            add(ListOfContactsMustBeNonEmpty.class);
        }
    };

    public static <T> CheckRunnerList<T> checkRunnersFor(Class<T> itemClass) {
        List<ClassBasedCheckRunner<T>> cbChecks = new ArrayList<ClassBasedCheckRunner<T>>();
        for (Class clazz : classBasedChecks) {
            Class typeArg = getTypeArgument(clazz);
            if (typeArg != null && (typeArg.equals(itemClass))) {
                cbChecks.add(new ClassBasedCheckRunner<T>(clazz));
            }
        }

        List<MethodBasedCheckRunner<T>> mbChecks = new ArrayList<MethodBasedCheckRunner<T>>();
        for (Class clazz : methodBasedChecks) {
            for (Method method : clazz.getMethods()) {
                MageTabCheck annot = method.getAnnotation(MageTabCheck.class);
                if (annot != null) {
                    mbChecks.add(new MethodBasedCheckRunner<T>(clazz, method));
                }
            }
        }
        return new CheckRunnerList<T>(mbChecks, cbChecks);
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

    /**
     * Returns {@link ParameterizedType} for {@link GlobalCheck} interface if the given class implements it;
     * otherswise <code>null</code>
     *
     * @param clazz the class to check
     * @return a {@link ParameterizedType} for {@link GlobalCheck} interface or <code>null</code>
     */
    protected static ParameterizedType getGlobalCheckInterface(Class<?> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type interf : interfaces) {
            Type rawType = ((ParameterizedType) interf).getRawType();
            if (rawType instanceof Class &&
                    rawType.equals(GlobalCheck.class)) {
                return (ParameterizedType) interf;
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

                    String typeVarName = ((Class) interf.getRawType()).getTypeParameters()[0].getName();
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
}

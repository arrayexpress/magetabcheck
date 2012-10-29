package uk.ac.ebi.fg.annotare2.magetab.checker;

import netscape.security.ParameterizedTarget;
import uk.ac.ebi.fg.annotare2.magetab.checks.idf.*;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class Checker {

    private final List<Class> simpleChecks = new ArrayList<Class>() {
        {
            add(IdfSimpleChecks.class);
        }
    };

    private final List<Class> globalChecks = new ArrayList<Class>() {
        {
            add(AtLeastOneContactMustBeSubmitter.class);
            add(AtLeastOneContactWithEmailRequired.class);
            add(AtLeastOneContactWithRolesRequired.class);
            add(AtLeastOneSubmitterMustHaveEmail.class);
            add(ListOfContactsMustBeNonEmpty.class);
        }
    };

    public void check(IdfData idf) {
        checkAll(new ArrayList<Person>(), Person.class);
    }

    private <T> void checkAll(Collection<T> collection, Class<T> itemClass) {
        List<GlobalCheck<T>> globals = new ArrayList<GlobalCheck<T>>();
        for (Class clazz : globalChecks) {
            Class type = getParameterType(clazz);
            if (type != null && (type.equals(Person.class))) {
                System.out.println("TODO");
            }
        }
    }

    private Class getParameterType(Class clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type interf : interfaces) {
            Type rawType = ((ParameterizedType) interf).getRawType();
            if (rawType instanceof Class &&
                    rawType.equals(GlobalCheck.class)) {
                return (Class) ((ParameterizedType) interf).getActualTypeArguments()[0];
            }
        }
        return null;
    }

    public static void main(String... args) {
        new Checker().check(null);
    }
}

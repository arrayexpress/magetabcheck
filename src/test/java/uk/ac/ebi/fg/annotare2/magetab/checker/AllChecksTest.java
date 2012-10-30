package uk.ac.ebi.fg.annotare2.magetab.checker;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.ac.ebi.fg.annotare2.magetab.checker.AllChecks.getTypeArgument;

/**
 * @author Olga Melnichuk
 */
public class AllChecksTest {

    @Test
    public void testDirectInterfaceInheritance() {
        class A implements GlobalCheck<Person> {
            @Override
            public void visit(Person person) {
            }
            @Override
            public void check() {
            }
        }

        Class<?> clazz = getTypeArgument(A.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }

    @Test
    public void testGenericSuperClassInheritance() {
        abstract class B<T> implements GlobalCheck<T> {}
        class A extends B<Person> {
            @Override
            public void visit(Person person) {
            }

            @Override
            public void check() {
            }
        }
        Class<?> clazz = getTypeArgument(A.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }

    @Test
    public void testGenericDepthSuperClassInheritance() {
        abstract class C<S,T> implements GlobalCheck<T> {}
        abstract class B<S> extends C<S, Person> {}
        class A extends B<Integer> {
            @Override
            public void visit(Person person) {
            }

            @Override
            public void check() {
            }
        }
        Class<?> clazz = getTypeArgument(A.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }
}

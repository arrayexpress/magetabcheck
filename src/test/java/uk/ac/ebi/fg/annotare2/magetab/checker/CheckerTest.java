package uk.ac.ebi.fg.annotare2.magetab.checker;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Olga Melnichuk
 */
public class CheckerTest {

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

        Class<?> clazz = Checker.getTypeArgument(A.class);
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
        Class<?> clazz = Checker.getTypeArgument(A.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }
}

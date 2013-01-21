package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;

import static org.junit.Assert.assertNotNull;

/**
 * @author Olga Melnichuk
 */
public class ClassBasedCheckDefinitionTest {

    private static InstanceProvider provider = new InstanceProvider() {
        @Override
        public <T> T newInstance(Class<T> clazz) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Can't create instance of class " + clazz);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't create instance of class " + clazz);
            }
        }
    };

    private static class B extends A<String> {
    }

    private static class A<T> {
        @Check
        public void check() {
        }

        @Visit
        public void visit(T s) {
        }
    }

    @Test
    public void test() {
        ClassBasedCheckDefinition def = new ClassBasedCheckDefinition(B.class, provider);
        assertNotNull(def.getMethodMarkedAsCheck());
        assertNotNull(def.getMethodMarkedAsVisit(String.class));
    }
}

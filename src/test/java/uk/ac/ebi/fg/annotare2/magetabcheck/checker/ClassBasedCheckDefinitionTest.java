package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;

import static org.junit.Assert.*;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.MICRO_ARRAY_ONLY;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ClassInstanceProvider.DEFAULT_CLASS_INSTANCE_PROVIDER;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType.HTS;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType.MICRO_ARRAY;

/**
 * @author Olga Melnichuk
 */
public class ClassBasedCheckDefinitionTest {
    @MageTabCheck(value = "class hierarchy check", application = MICRO_ARRAY_ONLY)
    private static class B extends A<String> {
    }

    private static class A<T> {
        @Visit
        public void visit(T t) {
        }

        @Check
        public void check() {
        }
    }

    @MageTabCheck(value = "plain single visit check", application = CheckApplicationType.HTS_ONLY)
    private static class C {
        @Visit
        public void visit(Integer i) {
        }
        @Check
        public void check() {
        }
    }

    @MageTabCheck(value = "plain muti-visit check", application = CheckApplicationType.ANY)
    private static class D {
        @Visit
        public void visi1(Integer i) {
        }
        @Visit
        public void visit2(String s) {
        }
        @Check
        public void check() {
        }
    }

    @Test
    public void testB() {
        ClassBasedCheckDefinition def = new ClassBasedCheckDefinition(B.class, DEFAULT_CLASS_INSTANCE_PROVIDER);
        assertNotNull(def.getMethodMarkedAsCheck());
        assertNotNull(def.getMethodMarkedAsVisit(String.class));
        assertNull(def.getMethodMarkedAsVisit(Integer.class));
        assertTrue(def.isApplicable(String.class, MICRO_ARRAY));
        assertFalse(def.isApplicable(Integer.class, MICRO_ARRAY));
        assertFalse(def.isApplicable(String.class, HTS));
    }

    @Test
    public void testC() {
        ClassBasedCheckDefinition def = new ClassBasedCheckDefinition(C.class, DEFAULT_CLASS_INSTANCE_PROVIDER);
        assertNotNull(def.getMethodMarkedAsCheck());
        assertNotNull(def.getMethodMarkedAsVisit(Integer.class));
        assertNull(def.getMethodMarkedAsVisit(String.class));
        assertTrue(def.isApplicable(Integer.class, HTS));
        assertFalse(def.isApplicable(String.class, HTS));
        assertFalse(def.isApplicable(String.class, MICRO_ARRAY));
    }

    @Test
    public void testD() {
        ClassBasedCheckDefinition def = new ClassBasedCheckDefinition(D.class, DEFAULT_CLASS_INSTANCE_PROVIDER);
        assertNotNull(def.getMethodMarkedAsCheck());
        assertNotNull(def.getMethodMarkedAsVisit(String.class));
        assertNotNull(def.getMethodMarkedAsVisit(Integer.class));
        assertTrue(def.isApplicable(String.class, MICRO_ARRAY));
        assertTrue(def.isApplicable(Integer.class, MICRO_ARRAY));
        assertTrue(def.isApplicable(String.class, HTS));
        assertTrue(def.isApplicable(Integer.class, HTS));
        assertNull(def.getMethodMarkedAsVisit(Long.class));
    }
}

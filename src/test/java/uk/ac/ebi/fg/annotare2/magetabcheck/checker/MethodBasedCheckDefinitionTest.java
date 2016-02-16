/*
 * Copyright 2012 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.*;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ClassInstanceProvider.DEFAULT_CLASS_INSTANCE_PROVIDER;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType.HTS;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType.MICRO_ARRAY;

/**
 * @author Olga Melnichuk
 */
public class MethodBasedCheckDefinitionTest {

    private static class MethodBasedChecks {
        @MageTabCheck(value = "check 1", application = MICRO_ARRAY_ONLY)
        public void check1(String s) {
        }

        @MageTabCheck(value = "check 2", application = HTS_ONLY)
        public void check2(Integer i) {
        }

        @MageTabCheck(value = "check 3", application = ANY)
        public void check3(Double d) {
        }
    }

    @Test
    public void test() {
        Method[] methods = MethodBasedChecks.class.getDeclaredMethods();
        Arrays.sort(methods, new CompareMethodsByName());

        MethodBasedCheckDefinition def1 = new MethodBasedCheckDefinition(methods[0], DEFAULT_CLASS_INSTANCE_PROVIDER);
        assertTrue(def1.isSubjectTypeAssignableFrom(String.class));
        assertFalse(def1.isSubjectTypeAssignableFrom(Integer.class));
        assertTrue(def1.isApplicable(String.class, MICRO_ARRAY));
        assertFalse(def1.isApplicable(Integer.class, MICRO_ARRAY));
        assertFalse(def1.isApplicable(String.class, HTS));

        MethodBasedCheckDefinition def2 = new MethodBasedCheckDefinition(methods[1], DEFAULT_CLASS_INSTANCE_PROVIDER);
        assertTrue(def2.isSubjectTypeAssignableFrom(Integer.class));
        assertFalse(def2.isSubjectTypeAssignableFrom(String.class));
        assertTrue(def2.isApplicable(Integer.class, HTS));
        assertFalse(def2.isApplicable(String.class, HTS));
        assertFalse(def2.isApplicable(Integer.class, MICRO_ARRAY));

        MethodBasedCheckDefinition def3 = new MethodBasedCheckDefinition(methods[2], DEFAULT_CLASS_INSTANCE_PROVIDER);
        assertTrue(def3.isSubjectTypeAssignableFrom(Double.class));
        assertFalse(def3.isSubjectTypeAssignableFrom(String.class));
        assertTrue(def3.isApplicable(Double.class, HTS));
        assertTrue(def3.isApplicable(Double.class, MICRO_ARRAY));
        assertFalse(def3.isApplicable(Integer.class, MICRO_ARRAY));
    }

    private class CompareMethodsByName implements Comparator<Method> {

        @Override
        public int compare(Method m1, Method m2) {
            if (null == m1 && null == m2) return 0;

            if (null == m1) return 1;

            if (null == m2) return -1;

            if (null == m1.getName() && null == m2.getName()) return 0;

            if (null == m1.getName()) return 1;

            if (null == m2.getName()) return -1;

            return m1.getName().compareTo(m2.getName());
        }

    }
}

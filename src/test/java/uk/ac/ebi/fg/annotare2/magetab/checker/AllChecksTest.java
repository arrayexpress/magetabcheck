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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetab.checker;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.ac.ebi.fg.annotare2.magetab.checker.AllChecks.getGlobalCheckTypeArgument;

/**
 * @author Olga Melnichuk
 */
public class AllChecksTest {

    static class A implements GlobalCheck<Person> {
        @Override
        public void visit(Person person) {
        }
        @Override
        public void check() {
        }
    }

    @Test
    public void testDirectInterfaceInheritance() {
        Class<?> clazz = getGlobalCheckTypeArgument(A.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }


    static abstract class B1<T> implements GlobalCheck<T> {}
    static class A1 extends B1<Person> {
        @Override
        public void visit(Person person) {
        }

        @Override
        public void check() {
        }
    }

    @Test
    public void testGenericSuperClassInheritance() {
        Class<?> clazz = getGlobalCheckTypeArgument(A1.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }

    static abstract class C2<S,T> implements GlobalCheck<T> {}
    static abstract class B2<S> extends C2<S, Person> {}
    static class A2 extends B2<Integer> {
        @Override
        public void visit(Person person) {
        }

        @Override
        public void check() {
        }
    }

    @Test
    public void testGenericDepthSuperClassInheritance() {
        Class<?> clazz = getGlobalCheckTypeArgument(A2.class);
        assertNotNull(clazz);
        assertEquals(Person.class, clazz);
    }
}

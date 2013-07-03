/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.services.efo;

import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class EfoDagTest {

    @Test
    public void testEmptyDag() {
        EfoDag dag = new EfoDag(new HashMap<String, EfoNode>());
        assertNull(dag.getNodeById("any"));
        assertTrue(dag.getRootNodes().isEmpty());
    }

    @Test
    public void testSimpleDag() {
        EfoNodeImpl node1 = new EfoNodeImpl("1");
        EfoNodeImpl node2 = new EfoNodeImpl("2");

        node1.addChild(node2);
        node2.addParent(node1);

        Map<String, EfoNode> map = new HashMap<String, EfoNode>();
        map.put(node1.getAccession(), node1);
        map.put(node2.getAccession(), node2);

        EfoDag dag = new EfoDag(map);

        assertEquals(node1, dag.getNodeById(node1.getAccession()));
        assertEquals(node2, dag.getNodeById(node2.getAccession()));

        Collection<EfoNode> roots = dag.getRootNodes();
        assertEquals(1, roots.size());
        assertEquals(node1, roots.iterator().next());
    }
}

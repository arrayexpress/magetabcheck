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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.Location;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.fg.annotare2.magetab.model.FileLocation;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public abstract class LimpopoBasedSdrfNode<T extends SDRFNode> extends ObjectWithAttributes implements SdrfGraphNode {

    private final SdrfHelper helper;

    private final T node;

    private final Location location;

    protected LimpopoBasedSdrfNode(T node, SdrfHelper helper) {
        super(helper);
        this.node = node;
        this.helper = helper;
        this.location = helper.getLocation(node);
    }

    @Override
    public Collection<? extends SdrfGraphNode> getChildNodes() {
        return helper.wrapNodes(forceCast(node.getChildNodes()));
    }

    @Override
    public Collection<? extends SdrfGraphNode> getParentNodes() {
        return helper.wrapNodes(forceCast(node.getParentNodes()));
    }

    private Collection<? extends SDRFNode> forceCast(Set<Node> nodes) {
        List<SDRFNode> list = newArrayList();
        for(Node n : nodes) {
            if (SDRFNode.class.isAssignableFrom(n.getClass())) {
                list.add((SDRFNode)n);
            }
        }
        return list;
    }

    @Override
    public int getLine() {
        return location.getLineNumber();
    }

    @Override
    public int getColumn() {
        return location.getColumn();
    }

    @Override
    public String getName() {
        return node.getNodeName();
    }

    protected T node() {
        return node;
    }

    protected TermSource termSource(String termSourceRef) {
        return helper.termSource(termSourceRef);
    }

    protected FileLocation location(String file) {
        return new FileLocation(helper.getFileRoot(), file);
    }
}

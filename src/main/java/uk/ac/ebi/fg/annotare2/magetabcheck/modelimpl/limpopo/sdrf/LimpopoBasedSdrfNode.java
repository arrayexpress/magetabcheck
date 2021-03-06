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

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.Location;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.FileLocation;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
abstract class LimpopoBasedSdrfNode<T extends SDRFNode> extends ObjectWithAttributes implements SdrfGraphNode {

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
    public String getFileName() {
        return helper.getSourceName();
    }

    @Override
    public String getName() {
        String name = node.getNodeName();
        // Annotare uses this pattern to mark not yet completed nodes
        if (null != name && name.matches("^____UNASSIGNED____\\d+$")) {
            name = "";
        }
        return name;
    }

    protected T node() {
        return node;
    }

    protected Protocol protocol(String protocolRef) {
        return helper.protocol(protocolRef);
    }

    protected TermSource termSource(String termSourceRef) {
        return helper.termSource(termSourceRef);
    }

    protected FileLocation location(String file) {
        // Annotare uses this pattern to make file nodes unique
        if (null != file && file.matches("^____.+____\\d+____$")) {
            file = file.replaceFirst("^____(.+)____\\d+____$", "$1");
        }

        return new FileLocation(helper.getSourceUrl(), file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LimpopoBasedSdrfNode that = (LimpopoBasedSdrfNode) o;

        if (node != null ? !node.equals(that.node) : that.node != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return node != null ? node.hashCode() : 0;
    }
}

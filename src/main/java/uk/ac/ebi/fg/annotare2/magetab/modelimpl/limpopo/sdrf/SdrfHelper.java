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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.Location;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SourceNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.CharacteristicsAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.MaterialTypeAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.ProviderAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetab.model.Identity;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraphAttribute;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class SdrfHelper {

    private final Map<Identity, SdrfGraphNode> mappedNodes = newHashMap();

    private final Map<Identity, SdrfGraphAttribute> mappedAttributes = newHashMap();

    private final SDRF sdrf;

    private final IdfData idf;

    public SdrfHelper(SDRF sdrf, IdfData idf) {
        this.sdrf = sdrf;
        this.idf = idf;
    }

    public SDRF sdrf() {
        return sdrf;
    }

    public <T extends SDRFNode> Location getLocation(T node) {
        Collection<Location> locations = sdrf().getLayout().getLocationsForNode(node);
        return locations.iterator().next();
    }

    public <T extends SDRFAttribute> Location getLocation(T attr) {
        Collection<Location> locations = sdrf().getLayout().getLocationsForAttribute(attr);
        return locations.iterator().next();
    }

    public Collection<SdrfGraphNode> wrapNodes(Collection<? extends SDRFNode> nodes) {
        List<SdrfGraphNode> wrapped = newArrayList();
        for (SDRFNode n : nodes) {
            wrapped.add(wrapNode(n));
        }
        return wrapped;
    }

    public Collection<SdrfGraphAttribute> wrapAttributes(Collection<? extends SDRFAttribute> attributes) {
        List<SdrfGraphAttribute> wrapped = newArrayList();
        for (SDRFAttribute a : attributes) {
            wrapped.add(wrapAttribute(a));
        }
        return wrapped;
    }

    private SdrfGraphAttribute wrapAttribute(SDRFAttribute attr) {
        Identity id = new Identity(attr);
        SdrfGraphAttribute wrappedAttr = mappedAttributes.get(id);
        if (wrappedAttr != null) {
            return wrappedAttr;
        }

        if (attr instanceof MaterialTypeAttribute) {
            wrappedAttr = new LimpopoBasedMaterialTypeAttribute((MaterialTypeAttribute) attr, this);
        } else if (attr instanceof CharacteristicsAttribute) {
            wrappedAttr = new LimpopoBasedCharacteristicAttribute((CharacteristicsAttribute) attr, this);
        } else if (attr instanceof ProviderAttribute) {
            wrappedAttr = new LimpopoBasedProviderAttribute((ProviderAttribute) attr, this);
        } else {
           wrappedAttr = new LimpopoBasedUnknownAttribute(attr, this);
        }
        mappedAttributes.put(id, wrappedAttr);
        return wrappedAttr;
    }

    private SdrfGraphNode wrapNode(SDRFNode node) {
        Identity id = new Identity(node);
        SdrfGraphNode wrappedNode = mappedNodes.get(id);
        if (wrappedNode != null) {
            return wrappedNode;
        }

        if (node instanceof SourceNode) {
            wrappedNode = new LimpopoBasedSourceNode((SourceNode) node, this);
        } else {
            wrappedNode = new LimpopoBasedUnknownNode(node, this);
        }
        mappedNodes.put(id, wrappedNode);
        return wrappedNode;
    }

    public TermSource termSource(String termSourceRef) {
        return idf.getTermSource(termSourceRef);
    }
}

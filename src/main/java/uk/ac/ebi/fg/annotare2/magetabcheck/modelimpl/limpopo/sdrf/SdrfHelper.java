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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.layout.Location;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.*;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.Identity;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.utils.Urls;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
class SdrfHelper {

    private final Map<Identity, SdrfGraphNode> mappedNodes = newHashMap();

    private final Map<Identity, SdrfGraphAttribute> mappedAttributes = newHashMap();

    private final SDRF sdrf;

    private final IdfData idf;

    private final String sourceName;

    public SdrfHelper(SDRF sdrf, IdfData idf) {
        this.sdrf = sdrf;
        this.idf = idf;
        this.sourceName = Urls.getFileName(sdrf.getLocation());
    }

    public <T extends SDRFNode> Location getLocation(T node) {
        Collection<Location> locations = sdrf.getLayout().getLocationsForNode(node);
        return locations.iterator().next();
    }

    public <T extends SDRFAttribute> Location getLocation(T attr) {
        Collection<Location> locations = sdrf.getLayout().getLocationsForAttribute(attr);
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
        } else if (attr instanceof PerformerAttribute) {
            wrappedAttr = new LimpopoBasedPerformerAttribute((PerformerAttribute) attr, this);
        } else if (attr instanceof ParameterValueAttribute) {
            wrappedAttr = new LimpopoBasedParameterValueAttribute((ParameterValueAttribute) attr, this);
        } else if (attr instanceof UnitAttribute) {
            wrappedAttr = new LimpopoBasedUnitAttribute((UnitAttribute) attr, this);
        } else if (attr instanceof LabelAttribute) {
            wrappedAttr = new LimpopoBasedLabelAttribute((LabelAttribute) attr, this);
        } else if (attr instanceof TechnologyTypeAttribute) {
            wrappedAttr = new LimpopoBasedTechnologyTypeAttribute((TechnologyTypeAttribute) attr, this);
        } else if (attr instanceof FactorValueAttribute) {
            wrappedAttr = new LimpopoBasedFactorValueAttribute((FactorValueAttribute) attr, this);
        } else if (attr instanceof ArrayDesignAttribute) {
            wrappedAttr = new LimpopoBasedArrayDesignAttribute((ArrayDesignAttribute) attr, this);
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
        } else if (node instanceof SampleNode) {
            wrappedNode = new LimpopoBasedSampleNode((SampleNode) node, this);
        } else if (node instanceof ExtractNode) {
            wrappedNode = new LimpopoBasedExtractNode((ExtractNode) node, this);
        } else if (node instanceof LabeledExtractNode) {
            wrappedNode = new LimpopoBasedLabeledExtractNode((LabeledExtractNode) node, this);
        } else if (node instanceof ProtocolApplicationNode) {
            wrappedNode = new LimpopoBasedProtocolNode((ProtocolApplicationNode) node, this);
        } else if (node instanceof AssayNode) {
            wrappedNode = new LimpopoBasedAssayNode((AssayNode) node, this);
        } else if (node instanceof HybridizationNode) {
            wrappedNode = new LimpopoBasedHybridizationNode((HybridizationNode) node, this);
        } else if (node instanceof ScanNode) {
            wrappedNode = new LimpopoBasedScanNode((ScanNode) node, this);
        } else if (node instanceof NormalizationNode) {
            wrappedNode = new LimpopoBasedNormalizationNode((NormalizationNode) node, this);
        } else if (node instanceof ArrayDataNode) {
            wrappedNode = new LimpopoBasedArrayDataNode((ArrayDataNode) node, this);
        } else if (node instanceof DerivedArrayDataNode) {
            wrappedNode = new LimpopoBasedDerivedArrayDataNode((DerivedArrayDataNode) node, this);
        } else if (node instanceof ArrayDataMatrixNode) {
            wrappedNode = new LimpopoBasedArrayDataMatrixNode((ArrayDataMatrixNode) node, this);
        } else if (node instanceof DerivedArrayDataMatrixNode) {
            wrappedNode = new LimpopoBasedDerivedArrayDataMatrixNode((DerivedArrayDataMatrixNode) node, this);
        } else {
            wrappedNode = new LimpopoBasedUnknownNode(node, this);
        }
        mappedNodes.put(id, wrappedNode);
        return wrappedNode;
    }

    public TermSource termSource(String termSourceRef) {
        return idf.getTermSource(termSourceRef);
    }

    public URL getSourceUrl() {
        return sdrf.getLocation();
    }

    public String getSourceName() {
        return sourceName == null ? "SDRF" : sourceName;
    }

    public Protocol protocol(String protocolRef) {
        return idf.getProtocol(protocolRef);
    }

    public Collection<? extends SdrfGraphNode> getRootNodes() {
        return wrapNodes(sdrf.getRootNodes());
    }
}

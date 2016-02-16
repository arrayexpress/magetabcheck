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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ProtocolApplicationNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfComment;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfParameterValueAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfPerformerAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfProtocolNode;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedProtocolNode extends LimpopoBasedSdrfNode<ProtocolApplicationNode>
        implements SdrfProtocolNode {

    public LimpopoBasedProtocolNode(ProtocolApplicationNode node, SdrfHelper helper) {
        super(node, helper);
    }

    @Override
    public String getTermSourceRef() {
        return node().termSourceREF;
    }

    @Override
    public TermSource getTermSource() {
        return termSource(node().termSourceREF);
    }

    @Override
    protected Collection<SDRFAttribute> getRawAttributes() {
        List<SDRFAttribute> attributes = newArrayList();
        attributes.add(node().performer);
        attributes.addAll(node().parameterValues);
        return attributes;
    }

    @Override
    public String getDate() {
        return node().date;
    }

    @Override
    public String getProtocolRef() {
        return node().protocol;
    }

    @Override
    public Protocol getProtocol() {
         return protocol(getProtocolRef());
    }

    @Override
    public SdrfPerformerAttribute getPerformer() {
        return getAttribute(SdrfPerformerAttribute.class);
    }

    @Override
    public Collection<SdrfParameterValueAttribute> getParameters() {
        return getAttributes(SdrfParameterValueAttribute.class);
    }

    @Override
    public Collection<SdrfComment> getComments() {
        List<SdrfComment> comments = newArrayList();
        for (String commentName : node().comments.keySet()) {
            comments.add(new LimpopoBasedSdrfComment(commentName, node().comments.get(commentName)));
        }
        return comments;
    }
}

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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.AssayNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedAssayNode extends LimpopoBasedSdrfNode<AssayNode> implements SdrfAssayNode {

    protected LimpopoBasedAssayNode(AssayNode node, SdrfHelper helper) {
        super(node, helper);
    }

    @Override
    protected Collection<SDRFAttribute> getRawAttributes() {
        List<SDRFAttribute> attributes = newArrayList();
        attributes.add(node().technologyType);
        attributes.addAll(node().factorValues);
        attributes.addAll(node().arrayDesigns);
        return attributes;
    }

    @Override
    public SdrfTechnologyTypeAttribute getTechnologyType() {
         return getAttribute(SdrfTechnologyTypeAttribute.class);
    }

    @Override
    public Collection<SdrfFactorValueAttribute> getFactorValues() {
        return getAttributes(SdrfFactorValueAttribute.class);
    }

    @Override
    public Collection<SdrfArrayDesignAttribute> getArrayDesigns() {
        return getAttributes(SdrfArrayDesignAttribute.class);
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

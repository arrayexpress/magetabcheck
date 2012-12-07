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

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfCharacteristicAttribute;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfMaterialNode;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfMaterialTypeAttribute;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
abstract class LimpopoBasedMaterialNode<T extends SDRFNode> extends LimpopoBasedSdrfNode<T> implements SdrfMaterialNode {

    protected LimpopoBasedMaterialNode(T node, SdrfHelper helper) {
        super(node, helper);
    }

    @Override
    public Collection<SdrfCharacteristicAttribute> getCharacteristics() {
        return getAttributes(SdrfCharacteristicAttribute.class);
    }

    @Override
    public SdrfMaterialTypeAttribute getMaterialType() {
        return getAttribute(SdrfMaterialTypeAttribute.class);
    }
}

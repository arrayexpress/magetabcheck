package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.AssayNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.HybridizationNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.ArrayDesignAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfArrayDesignAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfAssayNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfFactorValueAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfTechnologyTypeAttribute;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedHybridizationNode extends LimpopoBasedSdrfNode<HybridizationNode> implements SdrfAssayNode {

    protected LimpopoBasedHybridizationNode(HybridizationNode node, SdrfHelper helper) {
        super(node, helper);
    }

    @Override
    protected Collection<SDRFAttribute> getRawAttributes() {
        List<SDRFAttribute> attributes = newArrayList();
        attributes.addAll(node().arrayDesigns);
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
}

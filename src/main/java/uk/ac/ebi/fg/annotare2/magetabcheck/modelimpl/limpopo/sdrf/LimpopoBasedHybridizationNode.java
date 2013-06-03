package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.HybridizationNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedHybridizationNode extends LimpopoBasedSdrfNode<HybridizationNode> implements SdrfAssayNode {

    private SdrfTechnologyTypeAttribute technologyType;

    protected LimpopoBasedHybridizationNode(HybridizationNode node, final SdrfHelper helper) {
        super(node, helper);
        technologyType = new SdrfTechnologyTypeAttribute() {
            @Override
            public String getValue() {
                return "array assay";
            }

            @Override
            public String getName() {
                return "Technology Attribute";
            }

            @Override
            public Collection<? extends SdrfGraphAttribute> getAttributes() {
                return emptyList();
            }

            @Override
            public int getLine() {
                return 0;
            }

            @Override
            public int getColumn() {
                return 0;
            }

            @Override
            public String getFileName() {
                return helper.getSourceName();
            }

            @Override
            public String getTermSourceRef() {
                return null;
            }

            @Override
            public TermSource getTermSource() {
                return null;
            }
        };
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
        return technologyType;
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

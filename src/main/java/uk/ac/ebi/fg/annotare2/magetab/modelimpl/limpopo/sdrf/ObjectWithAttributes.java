package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.SDRFAttribute;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.HasAttributes;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraphAttribute;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public abstract class ObjectWithAttributes implements HasAttributes {

    private final SdrfHelper helper;

    protected ObjectWithAttributes(SdrfHelper helper) {
        this.helper = helper;
    }

    @Override
    public Collection<? extends SdrfGraphAttribute> getAttributes() {
        return helper.wrapAttributes(getRawAttributes());
    }

    @SuppressWarnings("unchecked")
    protected <T extends SdrfGraphAttribute> Collection<T> getAttributes(Class<T> clazz) {
        Collection<? extends SdrfGraphAttribute> allAttributes = getAttributes();
        List<T> filtered = newArrayList();
        for(SdrfGraphAttribute attr : allAttributes) {
            if(clazz.isAssignableFrom(attr.getClass())) {
                filtered.add((T)attr);
            }
        }
        return filtered;
    }

    protected <T extends SdrfGraphAttribute> T getAttribute(Class<T> clazz) {
        Collection<T> attributes = getAttributes(clazz);
        return attributes.isEmpty() ? null : attributes.iterator().next();
    }

    protected abstract Collection<SDRFAttribute> getRawAttributes();

}

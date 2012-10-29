package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedIdfObject {

    private final LimpopoIdfHelper helper;

    public LimpopoBasedIdfObject(@Nonnull LimpopoIdfHelper helper) {
        this.helper = helper;
    }

    protected IDF idf() {
        return helper.idf();
    }

    protected TermSource termSource(String name) {
        return helper.getTermSource(name);
    }

    protected String get(List<String> list, int index) {
        return list == null || index >= list.size() ? null : list.get(index);
    }
}

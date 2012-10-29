package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedTermSource extends LimpopoBasedIdfObject implements TermSource{

    private final int index;

    public LimpopoBasedTermSource(LimpopoIdfHelper helper, int index) {
        super(helper);
        this.index = index;
    }

    @Override
    public String getName() {
        return get(idf().termSourceName, index);
    }

    @Override
    public String getVersion() {
        return get(idf().termSourceVersion, index);
    }

    @Override
    public String getFile() {
        return get(idf().termSourceFile, index);
    }
}

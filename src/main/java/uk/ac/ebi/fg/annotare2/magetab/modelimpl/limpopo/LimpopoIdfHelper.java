package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class LimpopoIdfHelper {

    private final IDF idf;

    private final Map<String, TermSource> termSources = new HashMap<String, TermSource>();

    public LimpopoIdfHelper(@Nonnull IDF idf) {
        this.idf = idf;

        int size = this.idf.termSourceName.size();
        for (int i = 0; i < size; i++) {
            TermSource ts = new LimpopoBasedTermSource(this, i);
            termSources.put(ts.getName(), ts);
        }
    }

    IDF idf() {
        return idf;
    }

    TermSource getTermSource(String name) {
        return termSources.get(name);
    }

    List<TermSource> getTermSources() {
        List<TermSource> list = new ArrayList<TermSource>();
        list.addAll(termSources.values());
        return list;
    }

}

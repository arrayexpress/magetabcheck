package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedIdfData implements IdfData {

    private final LimpopoIdfHelper idfHelper;

    public LimpopoBasedIdfData(@Nonnull IDF idf) {
        this.idfHelper = new LimpopoIdfHelper(idf);
    }

    @Override
    public List<Person> getContacts() {
        int size = idfHelper.idf().personFirstName.size();
        List<Person> contacts = new ArrayList<Person>();
        for (int i = 0; i < size; i++) {
            contacts.add(new LimpopoBasedPerson(idfHelper, i));
        }
        return contacts;
    }

    @Override
    public List<TermSource> getTermSources() {
        return idfHelper.getTermSources();
    }

}

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedIdfData implements IdfData {

    private final IDF idf;

    public LimpopoBasedIdfData(IDF idf) {
        this.idf = idf;
    }

    @Override
    public List<Person> getContacts() {
        int size = idf.personFirstName.size();
        List<Person> contacts = new ArrayList<Person>();
        for (int i = 0; i < size; i++) {
            contacts.add(new LimpopoBasedPerson(idf, i));
        }
        return contacts;
    }
}

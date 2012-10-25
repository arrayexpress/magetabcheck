package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedPerson extends LimpopoBasedIdfObject implements Person  {

    private final int index;

    public LimpopoBasedPerson(IDF idf, int index) {
        super(idf);
        this.index = index;
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getMidInitials() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPhone() {
        return null;
    }

    @Override
    public String getFax() {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return null;
    }

    @Override
    public TermList getRoles() {
        return null;
    }
}

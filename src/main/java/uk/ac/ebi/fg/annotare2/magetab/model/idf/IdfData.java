package uk.ac.ebi.fg.annotare2.magetab.model.idf;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface IdfData {

    List<Person> getContacts();

    List<TermSource> getTermSources();
}

package uk.ac.ebi.fg.annotare2.magetab.checker;

/**
 * @author Olga Melnichuk
 */
public interface GlobalCheck<T> {

    void visit(T t);

    void check();
}

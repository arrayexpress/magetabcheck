package uk.ac.ebi.fg.annotare2.magetab.checker;

/**
 * @author Olga Melnichuk
 */
public interface GlobalCheck<T> {

    public void visit(T t);

    public void check();
}

package uk.ac.ebi.fg.annotare2.magetab.checker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Olga Melnichuk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MageTabCheck {

    String value();

    CheckModality modality() default CheckModality.ERROR;

    CheckApplicationType application() default CheckApplicationType.ANY;
}

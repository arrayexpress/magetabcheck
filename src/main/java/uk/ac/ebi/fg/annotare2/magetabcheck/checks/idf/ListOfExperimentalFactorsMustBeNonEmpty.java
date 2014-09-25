package uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf;

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.NonEmptyRangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ExperimentalFactor;

import javax.annotation.Nullable;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "EF01",
        value = "An experiment must have at least one experimental variable specified")
public class ListOfExperimentalFactorsMustBeNonEmpty extends NonEmptyRangeCheck<ExperimentalFactor> {
    public ListOfExperimentalFactorsMustBeNonEmpty() {
        super(new Predicate<ExperimentalFactor>() {
            @Override
            public boolean apply(@Nullable ExperimentalFactor factor) {
                return factor != null &&
                        factor.getType() != null &&
                        factor.getType().getName() != null &&
                        factor.getType().getName().getValue() != null &&
                        factor.getName() != null &&
                        factor.getName().getValue() != null;
            }
        });
    }
}
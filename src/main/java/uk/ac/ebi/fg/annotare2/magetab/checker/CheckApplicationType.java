package uk.ac.ebi.fg.annotare2.magetab.checker;

import java.util.EnumSet;

import static java.util.EnumSet.allOf;
import static java.util.EnumSet.of;
import static uk.ac.ebi.fg.annotare2.magetab.checker.InvestigationType.*;

/**
 * @author Olga Melnichuk
 */
public enum CheckApplicationType {
    ANY(allOf(InvestigationType.class)),
    HTS_ONLY(of(HTS)),
    MICRO_ARRAY_ONLY(of(MICRO_ARRAY));

    private final EnumSet<InvestigationType> enumSet;

    private CheckApplicationType(EnumSet<InvestigationType> enumSet) {
        this.enumSet = enumSet;
    }

    public boolean appliesTo(InvestigationType type) {
        return enumSet.contains(type);
    }
}

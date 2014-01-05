package uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.RangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.MageTabCheckEfo;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Ranges.singleton;
import static uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownProtocolHardware.isValidProtocolHardware;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "PR10",
        value = "Sequencing protocol must have sequencing hardware specified for HTS submissions",
        application = CheckApplicationType.HTS_ONLY,
        details = "`Protocol Hardware` field for the sequencing protocol must contain a comma separated list of protocol hardware used ([supported term sources](#protocol-hardware-list))")

public class SequencingProtocolHardwareRequired extends RangeCheck<Protocol> {

    @Inject
    public SequencingProtocolHardwareRequired(MageTabCheckEfo efo) {
        super(new SequencingProtocolHardwarePredicate(efo), singleton(1));
    }

    static class SequencingProtocolHardwarePredicate extends SequencingProtocolRequired.SequencingProtocolPredicate {

        private SequencingProtocolHardwarePredicate(MageTabCheckEfo efo) {
            super(efo);
        }

        @Override
        public boolean apply(@Nullable Protocol protocol) {
            return super.apply(protocol) && hasSequencingHardware(protocol);
        }

        private boolean hasSequencingHardware(Protocol protocol) {
            String hardware = protocol.getHardware().getValue();
            if (isNullOrEmpty(hardware)) {
                return false;
            }
            String[] v = hardware.trim().split("\\s*,\\s*");
            return isValidProtocolHardware(v);
        }
    }

}

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checks.RangeCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.MageTabCheckEfo;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Range.atLeast;
import static uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownProtocolHardware.isValidProtocolHardware;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "PR10",
        value = "Sequencing protocol must have sequencing hardware from the following list: 454 GS, 454 GS 20, 454 GS FLX, 454 GS FLX+, 454 GS FLX Titanium, 454 GS Junior, Illumina Genome Analyzer, Illumina Genome Analyzer II, Illumina Genome Analyzer IIx, Illumina HiSeq 2500, Illumina HiSeq 2000, Illumina HiSeq 1500, Illumina HiSeq 1000, Illumina MiSeq, Illumina HiScanSQ, HiSeq X Ten, NextSeq 500, Helicos HeliScope, AB SOLiD System, AB SOLiD System 2.0, AB SOLiD System 3.0, AB SOLiD 3 Plus System, AB SOLiD 4 System, AB SOLiD 4hq System, AB SOLiD PI System, AB 5500 Genetic Analyzer, AB 5500xl Genetic Analyzer, PacBio RS, PacBio RS II, Ion Torrent PGM, Ion Torrent Proton, MinION, GridION, AB 3730xL Genetic Analyzer, AB 3730 Genetic Analyzer, AB 3500xL Genetic Analyzer, AB 3500 Genetic Analyzer, AB 3130xL Genetic Analyzer, AB 3130 Genetic Analyzer, AB 310 Genetic Analyzer, unspecified",
        application = CheckApplicationType.HTS_ONLY,
        details = "`Protocol Hardware` field for the sequencing protocol must contain a comma separated list of protocol hardware used ([supported term sources](#protocol-hardware-list))")

public class SequencingProtocolHardwareRequired extends RangeCheck<Protocol> {

    @Inject
    public SequencingProtocolHardwareRequired(MageTabCheckEfo efo) {
        super(new SequencingProtocolHardwarePredicate(efo), atLeast(1));
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

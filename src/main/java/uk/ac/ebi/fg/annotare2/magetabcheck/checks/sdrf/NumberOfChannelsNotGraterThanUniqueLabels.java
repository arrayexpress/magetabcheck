package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfAssayNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfLabeledExtractNode;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.MICRO_ARRAY_ONLY;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckModality.WARNING;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "LE06",
        value = "Number of channels in the experiment should not exceed the number of labels (dyes used)",
        application = MICRO_ARRAY_ONLY,
        modality = WARNING)
public class NumberOfChannelsNotGraterThanUniqueLabels {

    private final Set<String> uniqueLabels = newHashSet();
    private int channels = 0;

    @Visit
    public void visit(SdrfLabeledExtractNode labeledExtract) {
        uniqueLabels.add(labeledExtract.getLabel().getValue());
    }

    @Visit
    public void visit(SdrfAssayNode assayNode) {
        Set<String> labels = newHashSet();
        for (SdrfGraphNode parent : assayNode.getParentNodes()) {
            if (SdrfLabeledExtractNode.class.isAssignableFrom(parent.getClass())) {
                SdrfLabeledExtractNode labeledExtract = (SdrfLabeledExtractNode) parent;
                labels.add(labeledExtract.getLabel().getValue());
            }
        }
        channels = max(channels, labels.size());
    }

    @Check
    public void check() {
        assertThat(channels, lessThanOrEqualTo(uniqueLabels.size()));
    }
}

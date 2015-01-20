package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfAssayNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfLabeledExtractNode;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
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
public class NumberOfChannelsNoGreaterThanNumberOfUniqueLabels {

    private final Set<String> uniqueLabels = newHashSet();
    private int maxNumberOfChannels = 0;

    @Visit
    public void visit(SdrfAssayNode assayNode) {
        List<String> labels = newArrayList();
        addLabels(labels, assayNode);
        uniqueLabels.addAll(labels);
        maxNumberOfChannels = max(maxNumberOfChannels, labels.size());
    }

    private void addLabels(List<String> labelSet, SdrfGraphNode node) {
        if (node instanceof SdrfLabeledExtractNode) {
            labelSet.add(((SdrfLabeledExtractNode)node).getLabel().getValue());
        } else {
            for (SdrfGraphNode parent : node.getParentNodes()) {
                addLabels(labelSet, parent);
            }
        }
    }

    @Check
    public void check() {
        assertThat(maxNumberOfChannels, lessThanOrEqualTo(uniqueLabels.size()));
    }
}

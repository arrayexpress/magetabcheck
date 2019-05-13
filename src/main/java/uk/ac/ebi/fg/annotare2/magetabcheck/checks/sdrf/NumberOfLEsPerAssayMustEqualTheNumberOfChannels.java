package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.HasLocation;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfAssayNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfLabeledExtractNode;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckApplicationType.MICRO_ARRAY_ONLY;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDynamicDetailSetter.setCheckDynamicDetail;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPositionSetter.setCheckPosition;

@MageTabCheck(
        ref = "AN08",
        value = "An assay must be connected to a number of distinctly labeled extracts that equals a number of channels (dyes used)",
        application = MICRO_ARRAY_ONLY)
@SuppressWarnings("unused")
public class NumberOfLEsPerAssayMustEqualTheNumberOfChannels {

    private static Function<SdrfLabeledExtractNode, String> GET_NODE_NAME = new Function<SdrfLabeledExtractNode, String>() {
        public String apply(@Nullable SdrfLabeledExtractNode node) {
            checkNotNull(node);
            return "'" + node.getName() + "'";
        }
    };

    private final Set<String> uniqueLabels = newHashSet();

    @Visit
    public void collectLabels(SdrfLabeledExtractNode labeledExtractNode) {
        uniqueLabels.add("'" + getLabel(labeledExtractNode) + "'");
    }

    @Visit
    public void check(SdrfAssayNode assayNode) {
        Collection<SdrfLabeledExtractNode> labeledExtractNodes = getConnectedLabeledExtractNodes(assayNode);
        Set<String> labels = newHashSet();
        for (SdrfLabeledExtractNode labeledExtractNode : labeledExtractNodes) {
            labels.add(getLabel(labeledExtractNode));
        }

        setCellPosition(assayNode);
        setCheckDynamicDetail("'" + assayNode.getName() + "' must be connected to " + uniqueLabels.size() +
                " labeled extract" + (1 == uniqueLabels.size() ? "" : "s") + " with labels " + Joiner.on(", ").join(uniqueLabels) +
                "; currently connected to " +
                Joiner.on(", ").join(transform(labeledExtractNodes, GET_NODE_NAME)));
        assertThat(labels.size(), equalTo(uniqueLabels.size()));
        assertThat(labeledExtractNodes.size(), equalTo(uniqueLabels.size()));
    }

    private Collection<SdrfLabeledExtractNode> getConnectedLabeledExtractNodes(SdrfGraphNode node) {
        List<SdrfLabeledExtractNode> labeledExtractNodes = newArrayList();
        if (node instanceof SdrfLabeledExtractNode) {
            labeledExtractNodes.add((SdrfLabeledExtractNode)node);
        } else {
            for (SdrfGraphNode parent : node.getParentNodes()) {
                labeledExtractNodes.addAll(getConnectedLabeledExtractNodes(parent));
            }
        }
        return labeledExtractNodes;
    }

    private String getLabel(SdrfLabeledExtractNode labeledExtractNode) {
        return null != labeledExtractNode.getLabel() ? labeledExtractNode.getLabel().getValue() : null;
    }

    private static <T extends HasLocation> void setCellPosition(T t) {
        setCheckPosition(t.getFileName(), t.getLine(), t.getColumn());
    }
}

package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.*;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDynamicDetailSetter.setCheckDynamicDetail;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "FV04",
        value = "Values of an experimental variable must vary, for compound+dose at least one must vary")
public class FactorValuesMustVary {

    private Multimap<String, String> assayFactorValues;
    private Multimap<String, String> scanFactorValues;
    private Set<SdrfSourceNode> sourceNodes;

    public FactorValuesMustVary() {
        assayFactorValues = HashMultimap.create();
        scanFactorValues = HashMultimap.create();
        sourceNodes = newHashSet();
    }

    @Visit
    public void visit(SdrfAssayNode assayNode) {
        addSourceNodes(assayNode);
        for (SdrfFactorValueAttribute attr : assayNode.getFactorValues()) {
            addFactorValueAttribute(assayFactorValues, attr);
        }
    }

    @Visit
    public void visit(SdrfScanNode scanNode) {
        addSourceNodes(scanNode);
        for (SdrfFactorValueAttribute attr : scanNode.getFactorValues()) {
            addFactorValueAttribute(scanFactorValues, attr);
        }
    }

    @Check
    public void check() {
        check(assayFactorValues);
        check(scanFactorValues);
    }

    private void addSourceNodes(SdrfGraphNode node) {
        if (node instanceof SdrfSourceNode) {
            sourceNodes.add((SdrfSourceNode)node);
        } else {
            for (SdrfGraphNode parent : node.getParentNodes()) {
                addSourceNodes(parent);
            }
        }
    }

    private void addFactorValueAttribute(Multimap<String, String> multimap, SdrfFactorValueAttribute attr) {
        String value = attr.getValue();
        multimap.put(attr.getType(), value == null ? "" : value.trim());
    }

    private void check(Multimap<String, String> multimap) {
        if (sourceNodes.size() > 1) {
            Set factors = multimap.keySet();
            SortedSet<String> offendingFactors = Sets.newTreeSet();

            // Compound and dose are dependant factors
            // If we have both only one needs to vary
            // If we have only one then it needs to vary
            int min = 0;
            if (factors.contains("dose")) {
                min++;
            }
            if (factors.contains("compound")) {
                min++;
            }

            // Min determines if we have compound and dose, or just one
            // Thus if we have both min equals 2 and we should have more unique values than min
            // to ensure values vary. This also catches the case where we have just dose or just compound
            if (factors.contains("compound") || factors.contains("dose")) {

                int numUniqueFactorValues = multimap.get("dose").size() + multimap.get("compound").size();

                // if values of compound and dose do not vary add to offending factors and then assert at end
                if (numUniqueFactorValues <= min) {
                    if (factors.contains("compound") && factors.contains("dose")) {
                        offendingFactors.add("dose");
                        offendingFactors.add("compound");
                    } else if (factors.contains("dose")) {
                        offendingFactors.add("dose");
                    } else {
                        offendingFactors.add("compound");
                    }
                }

            }

            // Check all factors other than compound and dose
            // Store non-varying factors

            for (String key : multimap.keySet()) {
                if (!key.equals("dose") && !key.equals("compound")) {

                    // If size is equal to one then the values of that factor do not vary
                    if (multimap.get(key).size() == 1) {
                        offendingFactors.add(key);
                    }
                }
            }

            //  Check non-varying factors
            if (!offendingFactors.isEmpty()) {

                // Format string that user sees
                StringBuilder sb = new StringBuilder();
                for (Iterator<String> it = offendingFactors.iterator(); it.hasNext(); ) {
                    sb.append('\'').append(it.next()).append('\'');
                    if (it.hasNext()) {
                        sb.append(", ");
                    }
                }
                setCheckDynamicDetail("offending factor" + (offendingFactors.size() > 1 ? "s" : "") + ": " + sb.toString());
            }
            // Throws error if offendingFactors contains entries
            assertThat(offendingFactors.size(), equalTo(0));
        }
    }
}

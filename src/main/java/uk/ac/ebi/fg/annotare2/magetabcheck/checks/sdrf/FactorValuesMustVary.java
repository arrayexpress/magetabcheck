package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfAssayNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfFactorValueAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfScanNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "FV04",
        value = "Values of a experimental factor must vary")
public class FactorValuesMustVary {

    private Multimap<String, String> assayFactorValues;
    private Multimap<String, String> scanFactorValues;

    public FactorValuesMustVary() {
        assayFactorValues = HashMultimap.create();
        scanFactorValues = HashMultimap.create();
    }

    @Visit
    public void visit(SdrfAssayNode assayNode) {
        for (SdrfFactorValueAttribute attr : assayNode.getFactorValues()) {
            addFactorValueAttribute(assayFactorValues, attr);
        }
    }

    @Visit
    public void visit(SdrfScanNode scanNode) {
        for (SdrfFactorValueAttribute attr : scanNode.getFactorValues()) {
            addFactorValueAttribute(scanFactorValues, attr);
        }
    }

    @Check
    public void check() {
        check(assayFactorValues);
        check(scanFactorValues);
    }

    private void addFactorValueAttribute(Multimap<String, String> multimap, SdrfFactorValueAttribute attr) {
        String value = attr.getValue();
        multimap.put(attr.getType(), value == null ? "" : value.trim());
    }

    private void check(Multimap<String, String> multimap) {
        for (String key : multimap.keySet()) {
            assertThat(multimap.get(key).size(), greaterThan(1));
        }
    }
}

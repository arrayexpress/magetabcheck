package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfAssayNode;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfFactorValueAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfScanNode;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDynamicDetailSetter.setCheckDynamicDetail;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Olga Melnichuk
 */
@MageTabCheck(
        ref = "FV04",
        value = "Values of an experimental factor must vary, for compound+dose at least one must vary")
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
        Set factors = multimap.keySet();

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

        // Min is determines if we have compound and dose, or just one
        // Thus if we have both min equals 2 and we should have more unique values than min
        // to ensure values vary. This also catches the case where we have just dose or just compound
        if (factors.contains("compound") || factors.contains("dose")) {
            assertThat((multimap.get("dose").size() + multimap.get("compound").size()), greaterThan(min));
        }

        // Check all factors other than compound and dose
        for (String key : multimap.keySet()) {
            if (!key.equals("dose") && !key.equals("compound"))  {
                setCheckDynamicDetail("Offending factor: " + key);
                assertThat(multimap.get(key).size(), greaterThan(1));
            }
        }
    }
}

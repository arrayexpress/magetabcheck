package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfCharacteristicAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfSourceNode;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by emma on 21/08/2014.
 */


@MageTabCheck(
        ref = "SR06",
        value = "A source (starting sample for the experiment) should have unique characteristic types e.g. a characteristic of type strain (Characteristics[strain]) should not appear twice or more, regardless of casing (strain/Strain/STRAIN)"
)
public class CharacteristicTypeMustVary {

    boolean wereDuplicatesSpotted = false;

    @Visit
    public void visit(SdrfSourceNode sourceNode) {
        if (!wereDuplicatesSpotted) {
            Set<String> usedAttributes = new HashSet<String>();
            for (SdrfCharacteristicAttribute attr : sourceNode.getCharacteristics()) {
                String attrType = attr.getType().toLowerCase();
                if (usedAttributes.contains(attrType)) {
                    wereDuplicatesSpotted = true;
                    break;
                }
                usedAttributes.add(attrType);
            }
        }
    }


    @Check
    public void check() {
        assertThat(wereDuplicatesSpotted, is(false));
    }
}

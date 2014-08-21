package uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf;

import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfCharacteristicAttribute;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfSourceNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by emma on 21/08/2014.
 */


@MageTabCheck(
        ref = "SR06",
        value = "A source (starting sample for the experiment) should have unique characteristic types e.g. a characteristic of type strain (Characteristics[strain]) should not appear twice or more, regardless of casing (strain/Strain/STRAIN)"
)
public class CharacteristicTypeMustVary {

    Collection<SdrfCharacteristicAttribute> characteristics;

    @Visit
    // Get our list of characteristics
    public void visit(SdrfSourceNode sourceNode) {
        characteristics = sourceNode.getCharacteristics();
    }

    @Check
    public void check() {
        check(characteristics);
    }

    // Logic is to compare number of characteristic types with a unique set of types
    private void check(Collection<SdrfCharacteristicAttribute> characteristics) {

        Set<String> uniques = new HashSet<String>();
        for (SdrfCharacteristicAttribute attr : characteristics) {
            uniques.add(attr.getType().toLowerCase());
        }
        assertThat(uniques.size(), equalTo(characteristics.size()));
    }

}

package uk.ac.ebi.arrayexpress2.magetab.validator;

import junit.framework.TestCase;
import org.mged.annotare.validator.AnnotareError;
import org.mged.annotare.validator.SemanticValidator;
import org.mged.magetab.error.ErrorItem;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.listener.ErrorItemListener;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 18/03/11
 */
public class TestSemanticValidator extends TestCase {
    private MAGETABParser parser;
    private AnnotareError errors;
    private Validator<MAGETABInvestigation> validator;

    private URL idfResource;

    public void setUp() {
//        idfResource = getClass().getClassLoader().getResource("E-MEXP-986/E-MEXP-986.idf.txt");
        try {
            idfResource = new URL("http://www.ebi.ac.uk/microarray-as/ae/files/E-TABM-886/E-TABM-886.idf.txt");
        }
        catch (MalformedURLException e) {
            fail();
        }
        validator = new SemanticValidator(idfResource.getFile());
        parser = new MAGETABParser(validator);
    }

    public void tearDown() {
        parser = null;
    }

    public void testParseAndValidate() {
        parser.addErrorItemListener(new ErrorItemListener() {
            public void errorOccurred(ErrorItem item) {
                System.out
                        .println("Next error -\t" + item.getErrorCode() + ":\t" + item.getMesg() + " " +
                                         item.getComment());
            }
        });

        try {
            parser.parse(idfResource);
        }
        catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }
}

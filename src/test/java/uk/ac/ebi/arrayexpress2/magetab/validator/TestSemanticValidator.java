package uk.ac.ebi.arrayexpress2.magetab.validator;

import junit.framework.TestCase;
import org.mged.annotare.validator.AnnotareError;
import org.mged.annotare.validator.SemanticValidator;
import org.mged.magetab.error.ErrorItem;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.listener.ErrorItemListener;
import uk.ac.ebi.arrayexpress2.magetab.parser.AbstractParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.Parser;

import java.io.File;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        idfResource = getClass().getClassLoader().getResource("E-MEXP-986/E-MEXP-986.idf.txt");
        try {
            validator = new SemanticValidator(new File(idfResource.toURI()).getAbsolutePath());
            parser = new MAGETABParser(validator);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        parser = null;
    }

    public void testParseAndValidate() {
        parser.addErrorItemListener(new ErrorItemListener() {
            public void errorOccurred(ErrorItem item) {
                System.out.println("Next error -\t" + item.getErrorCode() + ":\t" + item.getMesg() + " " + item.getComment());
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

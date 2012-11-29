package uk.ac.ebi.fg.annotare2.magetab.checks;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.model.FileLocation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.fg.annotare2.magetab.checks.matchers.IsValidFileLocation.isValidFileLocation;

/**
 * @author Olga Melnichuk
 */
public class IsValidFileLocationTest {

    @Test
    public void testHttpUrl() throws MalformedURLException {
        assertTrue(isValidFileLocation().matches(new FileLocation("http://www.google.com")));
        assertFalse(isValidFileLocation().matches(new FileLocation("http://www.google.comm")));
    }

    @Test
    public void testHttpsUrl() throws MalformedURLException {
        assertTrue(isValidFileLocation().matches(new FileLocation("https://www.google.com")));
        assertFalse(isValidFileLocation().matches(new FileLocation("https://www.google.comm")));
    }

    @Test
    public void testFilePath() throws IOException {
        File file = File.createTempFile("test", ".tmp");
        long timeStamp = System.currentTimeMillis();
        assertTrue(isValidFileLocation().matches(new FileLocation("file://" + file.getPath())));
        assertTrue(isValidFileLocation().matches(new FileLocation(file.getParentFile().toURI().toURL(), file.getName())));
        assertFalse(isValidFileLocation().matches(new FileLocation("file:///a/non/valid/path/" + timeStamp)));
        assertFalse(isValidFileLocation().matches(new FileLocation(file.getParentFile().toURI().toURL(), "" + timeStamp)));
    }
}

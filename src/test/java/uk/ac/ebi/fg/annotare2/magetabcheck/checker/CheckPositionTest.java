package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPosition.createPosition;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPosition.undefinedPosition;

/**
 * @author Olga Melnichuk
 */
public class CheckPositionTest {

    @Test
    public void testUndefinedPosition() {
        CheckPosition pos = undefinedPosition();
        assertTrue(pos.isUndefined());
        assertNull(pos.getFileName());
        assertTrue(pos.getLine() < 0);
        assertTrue(pos.getColumn() < 0);
    }

    @Test
    public void testCreatePosition() {
        CheckPosition pos = createPosition("file", 2, 3);
        assertFalse(pos.isUndefined());
        assertEquals("file", pos.getFileName());
        assertEquals(2, pos.getLine());
        assertEquals(3, pos.getColumn());
    }
}

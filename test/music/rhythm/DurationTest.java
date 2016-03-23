package music.rhythm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class DurationTest {
    @Test
    public void testConstructor() {
        Duration whole = new Duration(Duration.NoteValue.WHOLE, false);

        assertEquals("whole (1/1)", whole.toString());

        Duration dottedQuarter = new Duration(Duration.NoteValue.QUARTER, true);

        assertEquals("Dotted quarter (3/4)", dottedQuarter.toString());
    }

    @Test
    public void testMultipleDurations() {

    }

    @Test
    public void testToString() throws Exception {

    }
}
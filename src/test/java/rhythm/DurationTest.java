package rhythm;

import music.rhythm.Duration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class DurationTest {
    Duration whole = new Duration(Duration.NoteValue.WHOLE, false);
    Duration quarter = new Duration(Duration.NoteValue.QUARTER, false);
    Duration half = new Duration(Duration.NoteValue.HALF, false);
    Duration eighth = new Duration(Duration.NoteValue.EIGHTH, false);
    Duration dottedQuarter = new Duration(Duration.NoteValue.QUARTER, true);
    Duration dottedHalf = new Duration(Duration.NoteValue.HALF, true);


    @Test
    public void testConstructor() {

        assertEquals("Duration: whole", whole.toString());

        assertEquals("Duration: Dotted half", dottedHalf.toString());
    }

    @Test
    public void testAddDurations() {
        Duration result = Duration.addDurations(quarter, half);

        assertEquals(dottedHalf, result);

        result = Duration.addDurations(eighth, eighth, eighth);

        assertEquals(dottedQuarter, result);
    }
}
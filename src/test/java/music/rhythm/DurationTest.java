package music.rhythm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class DurationTest {
    private Duration whole = new Duration(Duration.DurationValue.WHOLE, false);
    private Duration quarter = new Duration(Duration.DurationValue.QUARTER, false);
    private Duration half = new Duration(Duration.DurationValue.HALF, false);
    private Duration eighth = new Duration(Duration.DurationValue.EIGHTH, false);
    private Duration dottedQuarter = new Duration(Duration.DurationValue.QUARTER, true);
    private Duration dottedHalf = new Duration(Duration.DurationValue.HALF, true);


    @Test
    public void testConstructor() {

        assertEquals("Duration: whole", whole.toString());

        assertEquals("Duration: dotted half", dottedHalf.toString());
    }

    @Test
    public void testAddDurations() {
        Duration result = Duration.addDurations(quarter, half);

        assertEquals(dottedHalf, result);

        result = Duration.addDurations(eighth, eighth, eighth);

        assertEquals(dottedQuarter, result);

        assertEquals(whole, Duration.addDurations(quarter, quarter, half));
    }

    @Test
    public void testRatio() {
        assertTrue(0.5 == quarter.getDurationRatio(half));
        assertTrue(2 == half.getDurationRatio(quarter));
        assertTrue(4 == whole.getDurationRatio(quarter));
    }
}
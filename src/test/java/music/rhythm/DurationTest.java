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
        Duration result = Duration.addDurations(quarter, half)[0];

        assertEquals(dottedHalf, result);

        result = Duration.addDurations(eighth, eighth, eighth)[0];

        assertEquals(dottedQuarter, result);

        assertEquals(whole, Duration.addDurations(quarter, quarter, half)[0]);

        Duration[] resultList = Duration.addDurations(whole, half, quarter);

        double sum = 0.0;
        for (Duration d: resultList) {
            sum += d.getDoubleValue();
        }

        assertTrue(1.75 == sum);
    }

    @Test
    public void testRatioGetter() {
        assertTrue(0.5 == quarter.getDurationRatio(half));
        assertTrue(2 == half.getDurationRatio(quarter));
        assertTrue(4 == whole.getDurationRatio(quarter));
    }

    @Test
    public void testDurationByRatio() {
        assertEquals(eighth, Duration.getDurationByRatio(quarter, 0.5));
        assertEquals(eighth, Duration.getDurationByRatio(half, 0.25));
        assertEquals(half, Duration.getDurationByRatio(quarter, 2.0));
        assertEquals(eighth, Duration.getDurationByRatio(whole, 0.125));
    }
}
package music.rhythm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by reedtrevelyan on 3/23/16.
 */
public class RestTest {

    @Test
    public void testConstructor() {
        Rest r = new Rest(new Duration(Duration.DurationValue.EIGHTH, true));

        assertEquals("Rest, Duration: dotted eighth", r.toString());

        Rest r2 = new Rest(new Duration(Duration.DurationValue.EIGHTH, true));

        assertEquals(r, r2);

    }

}
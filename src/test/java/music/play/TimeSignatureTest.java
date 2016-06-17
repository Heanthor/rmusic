package music.play;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Reed.t on 6/17/16.
 */
public class TimeSignatureTest {

    @Test
    public void testConstructor() {
        TimeSignature common = new TimeSignature(4, TimeSignature.DenominatorChoices._4);
        TimeSignature threefour = new TimeSignature(3, TimeSignature.DenominatorChoices._4);
        TimeSignature twelveeight = new TimeSignature(12, TimeSignature.DenominatorChoices._8);

        assertEquals(common, new TimeSignature("C"));

        assertEquals(threefour, new TimeSignature("3/4"));

        assertEquals(twelveeight, new TimeSignature("12/8"));
    }
}
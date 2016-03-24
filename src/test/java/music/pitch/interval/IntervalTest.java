package music.pitch.interval;

import music.pitch.Pitch;
import org.junit.Test;
import util.NoteBank;

import static music.pitch.Pitch.*;
import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class IntervalTest {
    @Test
    public void testIntervalEquals() {
        assertEquals(MajorIntervals._2nd.getNumHalfSteps(), DiminishedIntervals._3rd.getNumHalfSteps());
    }

    @Test
    public void testAddingInterval() {
        assertEquals(NoteBank.e, getNoteAbove(NoteBank.cs, MinorIntervals._3rd));

        // Tests wrap around
        assertEquals(NoteBank.c4, getNoteAbove(NoteBank.g3, PerfectIntervals.PERFECT_FOURTH));

        assertEquals(NoteBank.c5, getNoteAbove(NoteBank.c4, PerfectIntervals.OCTAVE));

        // A and G are stranger corner cases
        assertEquals(NoteBank.c4, getNoteAbove(NoteBank.ab, MajorIntervals._3rd));

        // A and G are stranger corner cases
        assertEquals(NoteBank.b4, getNoteAbove(NoteBank.ab, MinorIntervals._3rd));
    }

    @Test
    public void testIntervalBetween() {
        assertEquals(MinorIntervals._3rd.getNumHalfSteps(), getIntervalBetween(NoteBank.ab, NoteBank.b4).getNumHalfSteps());

        assertEquals(MajorIntervals._3rd.getNumHalfSteps(), getIntervalBetween(NoteBank.ab, NoteBank.c4).getNumHalfSteps());

        assertEquals(PerfectIntervals.PERFECT_FOURTH.getNumHalfSteps(), getIntervalBetween(NoteBank.g3, NoteBank.c4).getNumHalfSteps());

        assertEquals(PerfectIntervals.OCTAVE.getNumHalfSteps(), getIntervalBetween(NoteBank.c4, NoteBank.c5).getNumHalfSteps());
    }
}
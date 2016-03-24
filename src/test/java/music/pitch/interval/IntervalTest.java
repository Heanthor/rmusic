package music.pitch.interval;

import music.pitch.Pitch;
import org.junit.Test;
import util.NoteBank;

import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class IntervalTest {
    @Test
    public void testAddingInterval() {
        assertEquals(NoteBank.e, Pitch.getNoteAbove(NoteBank.cs, MinorIntervals._3rd));
        // Tests wrap around
        assertEquals(NoteBank.c4, Pitch.getNoteAbove(NoteBank.g3, PerfectIntervals.PERFECT_FOURTH));
    }
}
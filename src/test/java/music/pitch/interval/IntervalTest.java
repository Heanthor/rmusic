package music.pitch.interval;

import music.pitch.Note;
import org.junit.Test;
import util.NoteBank;

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
        assertEquals(NoteBank.e, IntervalUtils.getNoteAbove(NoteBank.cs, MinorIntervals._3rd));

        assertEquals(NoteBank.c4, IntervalUtils.getNoteAbove(NoteBank.g3, PerfectIntervals.PERFECT_FOURTH));

        assertEquals(NoteBank.c5, IntervalUtils.getNoteAbove(NoteBank.c4, PerfectIntervals.OCTAVE));

        assertEquals(NoteBank.c5, IntervalUtils.getNoteAbove(NoteBank.ab, MajorIntervals._3rd));

        assertEquals(NoteBank.b4, IntervalUtils.getNoteAbove(NoteBank.ab, MinorIntervals._3rd));

        // Test wrap around, corner cases
        assertEquals(new Note("Eb4:Q"), IntervalUtils.getNoteAbove(NoteBank.c4, MinorIntervals._3rd));
    }

    @Test
    public void testIntervalBelow() {
        assertEquals(NoteBank.cs, IntervalUtils.getNoteBelow(NoteBank.e, MinorIntervals._3rd));

        assertEquals(NoteBank.g3, IntervalUtils.getNoteBelow(NoteBank.c4, PerfectIntervals.PERFECT_FOURTH));

        assertEquals(new Note("A3:Q"), IntervalUtils.getNoteBelow(NoteBank.c4, MinorIntervals._3rd));

        assertEquals(new Note("F4:Q"), IntervalUtils.getNoteBelow(new Note("Ab4:Q"), MinorIntervals._3rd));
    }

    @Test
    public void testIntervalBetween() {
        assertEquals(MinorIntervals._3rd.getNumHalfSteps(), IntervalUtils.getIntervalBetween(NoteBank.ab, NoteBank.b4).getNumHalfSteps());

        assertEquals(MajorIntervals._3rd.getNumHalfSteps(), IntervalUtils.getIntervalBetween(NoteBank.ab, NoteBank.c5).getNumHalfSteps());

        assertEquals(PerfectIntervals.PERFECT_FOURTH.getNumHalfSteps(), IntervalUtils.getIntervalBetween(NoteBank.g3, NoteBank.c4).getNumHalfSteps());

        assertEquals(PerfectIntervals.OCTAVE.getNumHalfSteps(), IntervalUtils.getIntervalBetween(NoteBank.c4, NoteBank.c5).getNumHalfSteps());
    }
}
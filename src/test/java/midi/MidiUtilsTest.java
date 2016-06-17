package midi;

import music.pitch.Note;
import music.play.Tempo;
import music.rhythm.Duration;
import org.junit.Test;
import util.NoteBank;

import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class MidiUtilsTest {
    @Test
    public void getMidiNoteTest() throws Exception {
        assertEquals(60, MidiUtils.getMidiNote(NoteBank.c4));
        assertEquals(61, MidiUtils.getMidiNote(NoteBank.cs4));
        assertEquals(84, MidiUtils.getMidiNote(new Note("B#5")));
        assertEquals(24, MidiUtils.getMidiNote(new Note("Cb4")));

        assertEquals(66, MidiUtils.getMidiNote(new Note("Gb4")));
    }

    @Test
    public void getNoteFromMidiTest() throws Exception {
        assertEquals(NoteBank.c4, MidiUtils.getNoteFromMidi(60));
        assertEquals(NoteBank.bs4, MidiUtils.getNoteFromMidi(72));
        assertEquals(NoteBank.gs, MidiUtils.getNoteFromMidi(68));
    }

    @Test
    public void testMidiDuration() {
        Duration quarter = new Duration(Duration.DurationValue.QUARTER, false);
        Tempo t = new Tempo(quarter, 120);

        int result = MidiUtils.getDurationMiliseconds(quarter, t);
        assertTrue(500 == result);

        result = MidiUtils.getDurationMiliseconds(new Duration(Duration.DurationValue.HALF, false), t);
        assertTrue(1000 == result);
    }
}
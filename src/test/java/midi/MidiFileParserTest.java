package midi;

import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import music.play.Voice;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by reedtrevelyan on 12/9/17.
 */
public class MidiFileParserTest {
    private File basic0 = null;
    private File basic1 = null;
    private File basic2 = null;
    private File basic3 = null;
    private File basic4 = null;
    private File basic5 = null;

    @Before
    public void setUp() throws Exception {
        basic0 = new File("bin/midifiles/test/basic_0.mid");
        basic1 = new File("bin/midifiles/test/basic_1.mid");
        basic2 = new File("bin/midifiles/test/basic_2.mid");
        basic3 = new File("bin/midifiles/test/basic_3.mid");
        basic4 = new File("bin/midifiles/test/basic_4.mid");
        basic5 = new File("bin/midifiles/test/basic_5.mid");

    }

    @Test
    public void testBasicNotes() {
        testMidi(basic0, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("D5:H,R:Q,D5:Q")}));
    }

    @Test
    public void testSimpleMelody() {
        testMidi(basic1, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("G5:Q,D5:Q,R:Q,G5:Q")}));
    }

    @Test
    public void testMultipleVoices() {
        testMidi(basic2, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{
                        new Voice("C5:H"),
                        new Voice("R:E,E5:Qd"),
                        new Voice("R:Q,G5:Q"),
                        new Voice("R:Hd,C6:E")}));
    }

    @Test
    public void testSingleVoiceOctaves() {
        testMidi(basic3, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{new Voice("C5:S,C6:S,B4:S,B5:S,A#4:S,A#5:S,A4:S,A5:S")}));
    }

    @Test
    public void testMultipleVoicesWithRest() {
        testMidi(basic4, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{
                        new Voice("D5:H,R:E,D#5:Qd"),
                        new Voice("D#5:E,R:Qd,D5:Q")}));
    }

    @Test
    public void testChordsRests() {
        testMidi(basic5, new Staff(new Tempo(110), null, new TimeSignature("C"),
                new Voice[]{
                        new Voice("F5:Q,R:Q,F5:E,R:E,F5:E"),
                        new Voice("D5:Q,R:Q,D5:E,R:E,D5:E"),
                        new Voice("B4:Q,R:Q,B4:E,R:E,B4:E"),
                        new Voice("G4:Q,R:Q,G4:E,R:E,G4:E")}));
    }

    /**
     * Test helper.
     *
     * @param f        Midi file to open
     * @param expected Expected conversion
     */
    private void testMidi(File f, Staff expected) {
        MidiFileParser m = new MidiFileParser();
        Staff parsed = null;

        try {
            parsed = m.loadAndParseFile(f);
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, parsed);
    }
}

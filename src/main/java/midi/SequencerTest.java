package midi;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

/**
 * @author reedt
 */
public class SequencerTest {
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        Sequencer s = MidiSystem.getSequencer();
        Sequence furElise = MidiSystem.getSequence(new File("bin/midifiles/for_elise_by_beethoven.mid"));
        Sequence basic1 = MidiSystem.getSequence(new File("bin/midifiles/test/basic_0.mid"));

        s.open();
        s.setSequence(basic1);
        System.out.println(s.getTempoInBPM());

        s.addMetaEventListener(System.out::println);
        s.start();
    }
}

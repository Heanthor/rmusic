package midi;

import music.pitch.Note;
/**
 * @author reedt
 */
public class MidiUtils {
    private MidiUtils() {}

    public static int getMidiNote(Note n) {
        // C2 == 36
        // C3 == 48
        // ...
        // Middle c (C4) == 60

        int octave = (n.octave.getNumberValue() + 1) * 12;

        return -1;
    }
}

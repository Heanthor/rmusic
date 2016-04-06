package midi;

import music.pitch.Note;
import music.pitch.NoteValue;
import music.pitch.Octave;
import music.pitch.Pitch;
import music.play.Tempo;
import music.rhythm.Duration;

/**
 * @author reedt
 */
public class MidiUtils {
    private MidiUtils() {
    }

    public static int getMidiNote(Note n) {
        // C2 == 36
        // C3 == 48
        // ...
        // Middle c (C4) == 60

        int octave = (n.octave.getNumberValue() + 1) * 12;

        // Corner cases
        if (n.basePitch == NoteValue.B && n.accidental == NoteValue.Accidental.SHARP) {
            return octave + 12;
        } else if (n.basePitch == NoteValue.C && n.accidental == NoteValue.Accidental.FLAT) {
            return (n.octave.getNumberValue() - 2) * 12;
        } else {
            int offset = n.pitchValue - 1;
            return octave + offset;
        }
    }

    public static Note getNoteFromMidi(int midiNote) {
        int octave = midiNote / 12;

        // Convert from midi format to our format with +1
        int offset = (midiNote % 12) + 1;

        Note temp = Pitch.pitchFromPitchValue(offset);

        return new Note(temp.basePitch, temp.accidental, Octave.octaveFromInteger(octave - 1));
    }

    public static int getDurationMiliseconds(Duration d, Tempo t) {
        double msBeatValue = (1 / (double)t.bpm) * 60000;

        double ratio = d.getDurationRatio(t.note);

        return (int)(ratio * msBeatValue);
    }
}

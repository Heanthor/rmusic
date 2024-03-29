package music.pitch;

import music.rhythm.Duration;

/**
 * Represents a specific note, containing information about its exact pitch.
 * A Note has a base pitch, an accidental, an octave, and a duration.
 * <p>
 * For example, a Note could have a base pitch A, accidental flat, exist in octave 4, and have a duration
 * of half note. This forms the notestring Ab4:H.
 *
 * @author reedt
 */
public class Note implements BasicNote, Comparable<Note> {
    // These fields can be used to identify the Note
    public final NoteValue basePitch;
    public final NoteValue.Accidental accidental;
    public final Octave octave;
    private int index = -1;

    private Duration duration;

    // This field provides ease of comparison between Notes
    public final int pitchValue;

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     *
     * @param basePitch  The base pitch, A-G
     * @param accidental Any accidental on the pitch
     * @param o          The octave it exists in, in scientific pitch notation
     * @param d          The duration of the note
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental, Octave o, Duration d) {
        this.basePitch = basePitch;
        this.accidental = accidental;
        this.pitchValue = Pitch.getPitchValue(basePitch, accidental);
        this.octave = o;
        this.duration = d;
    }

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     *
     * @param basePitch  The base pitch, A-G
     * @param accidental Any accidental on the pitch
     * @param o          The octave it exists in, in scientific pitch notation
     * @param d          The duration of the note
     * @param index      The midi index
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental, Octave o, Duration d, int index) {
        this(basePitch, accidental, o, d);
        this.index = index;
    }

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     * Its duration is set to quarter note.
     *
     * @param basePitch  The base pitch, A-G
     * @param accidental Any accidental on the pitch
     * @param o          The octave it exists in, in scientific pitch notation
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental, Octave o) {
        this.basePitch = basePitch;
        this.accidental = accidental;
        this.pitchValue = Pitch.getPitchValue(basePitch, accidental);
        this.octave = o;
        this.duration = new Duration(Duration.DurationValue.QUARTER, false);
    }

    /**
     * Create a note object, representing a specific pitch on a keyboard.
     * Its duration is set to quarter note.
     *
     * @param basePitch  The base pitch, A-G
     * @param accidental Any accidental on the pitch
     * @param o          The octave it exists in, in scientific pitch notation
     * @param index      The midi index
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental, Octave o, int index) {
        this(basePitch, accidental, o);
        this.index = index;
    }

    /**
     * Create a note object, representing a specific pitch, without octave specification.
     * Its duration is set to quarter note.
     *
     * @param basePitch  The base pitch, A-G
     * @param accidental Any accidental on the pitch
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental) {
        this.basePitch = basePitch;
        this.accidental = accidental;
        this.pitchValue = Pitch.getPitchValue(basePitch, accidental);
        this.octave = Octave.FOUR;
        this.duration = new Duration(Duration.DurationValue.QUARTER, false);
    }

    /**
     * Create a note object, representing a specific pitch, without octave specification.
     * Its duration is set to quarter note.
     *
     * @param basePitch  The base pitch, A-G
     * @param accidental Any accidental on the pitch
     * @param index      The midi index
     */
    public Note(NoteValue basePitch, NoteValue.Accidental accidental, int index) {
        this(basePitch, accidental);
        this.index = index;
    }

    /**
     * Parses a note string and creates its Note object representation.
     * Format: [Base note][Optional: # or b][Octave number]:[Optional: Duration string]
     *
     * @param noteStringIn Note representation string
     */
    public Note(String noteStringIn) {
        NoteValue nv;
        NoteValue.Accidental mod;
        Octave oct;
        Duration dur;
        String noteString;

        if (noteStringIn.indexOf(':') == -1) {
            // has no duration value
            dur = new Duration(Duration.DurationValue.QUARTER, false);
            noteString = noteStringIn;
        } else {
            // has a duration value
            dur = Duration.parseDurationString(noteStringIn.substring(noteStringIn.indexOf(':') + 1));
            noteString = noteStringIn.substring(0, noteStringIn.indexOf(':'));
        }

        try {
            nv = NoteValue.valueOf(("" + noteString.charAt(0)).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Note string format incorrect (Note name not found in first position).");
        }

        if (noteString.length() == 2) {
            // Note is natural, so last character must be octave
            mod = NoteValue.Accidental.NATURAL;

            try {
                // Octave constructor handles out of range octaves
                oct = Octave.octaveFromInteger(Integer.parseInt("" + noteString.charAt(1)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Note string format incorrect (No octave found).");
            }

        } else if (noteString.length() == 3) {
            // Note has modifications
            char modification = noteString.charAt(1);

            mod = switch (modification) {
                case '#' -> NoteValue.Accidental.SHARP;
                case 'b' -> NoteValue.Accidental.FLAT;
                default -> throw new IllegalArgumentException("Note string format incorrect (note modification incorrect). " +
                        "Pick either: 'b' or '#'.");
            };

            try {
                oct = Octave.octaveFromInteger(Integer.parseInt("" + noteString.charAt(2)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Note string format incorrect (no octave found).");
            }

        } else {
            throw new IllegalArgumentException("Note string format incorrect (incorrect length - missing parameters).");
        }

        this.basePitch = nv;
        this.accidental = mod;
        this.octave = oct;
        this.pitchValue = Pitch.getPitchValue(nv, mod);
        this.duration = dur;
    }

    /**
     * Parses a note string and creates its Note object representation.
     * Format: [Base note (uppercase)][Optional: # or b][Octave number]:[Duration string]
     *
     * @param noteString Note representation string
     * @param index      The midi index
     */
    public Note(String noteString, int index) {
        this(noteString);
        this.index = index;
    }

    /**
     * Formats a note or rest into a compact format, exactly 7 characters long, e.g.:
     * The note E5, duration sixteenth note: E5:S...
     * D#5 sixteenth: D#5:S..
     * @param b The Note or Rest to parse
     * @return a 7 character long string representing the note or rest in a notestring form.
     */
    public static String compactFormatBasicNote(BasicNote b) {
        return String.format("%-7s", b.toNoteString()).replace(' ', '.');
    }

    /**
     * @return String in form "Pitch: [base pitch][accidental], Duration: [duration]"
     */
    @Override
    public String toString() {
        String toReturn = "Pitch: " + pitchOnlyToString();

        toReturn += ", " + duration.toString();

        return toReturn;
    }

    /**
     * @return String in the form "[base pitch][accidental]"
     */
    public String pitchOnlyToString() {
        String toReturn = basePitch.name();

        switch (accidental) {
            case SHARP -> toReturn += "#";
            case FLAT -> toReturn += "b";
            default -> {
            }
            // Add nothing for natural
        }

        toReturn += octave.toString();

        return toReturn;
    }

    @Override
    public String toNoteString() {
        return pitchOnlyToString() + ":" + duration.getDurationCode();
    }

    /**
     * A note is equal to another note when it represents the same sounding pitch,
     * as well as the same duration.
     * Thus, this method checks for enharmonic equivalence between two pitches.
     *
     * @param obj Note to compare
     * @return True if notes are enharmonically equivalent, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Note)) {
            return false;
        }

        Note n = (Note) obj;

        // Notes must have the same duration to be equal
        return this.duration.equals(n.duration) && equalsPitchOnly(n);
    }

    /**
     * Determines if two notes are equal, ignoring duration.
     * A note is equal to another note when it represents the same sounding pitch,
     * as well as the same duration.
     *
     * @param n Note to compare
     * @return True if notes represent the same pitch, false otherwise.
     */
    public boolean equalsPitchOnly(Note n) {
        // Notes must be in the same octave to be equal

        // Octave changes on C, corner case
        if (this.basePitch == NoteValue.C || n.basePitch == NoteValue.C) {
            // Not more than one octave separates them
            if (Math.abs(n.octave.getNumberValue() - this.octave.getNumberValue()) > 1) {
                return false;
            }
        }

        // No corner case; notes are equal, octaves aren't
        if (!(this.octave.equals(n.octave)) && this.pitchValue == n.pitchValue) {
            return false;
        }

        // Enharmonic notes are equal pitch even though notated differently
        // Special case for wrapping around B# and C, or Cb and B
        return this.pitchValue == 13 && n.pitchValue == 1
                || this.pitchValue == 1 && n.pitchValue == 13
                || this.pitchValue == 0 && n.pitchValue == 12
                || this.pitchValue == 12 && n.pitchValue == 0
                || this.pitchValue == n.pitchValue;
    }

    /**
     * A note is "less than" another note when it is a lower pitch, and higher when its pitch is higher.
     *
     * @param o Note to compare to
     * @return Negative, zero, or positive based on comparison
     */
    @Override
    public int compareTo(Note o) {
        // Covers enharmonic equivalence
        if (this.equals(o)) {
            return 0;
        }

        if (this.octave.getNumberValue() == o.octave.getNumberValue()) {
            // In same octave
            return this.pitchValue - o.pitchValue;
        } else {
            // In different octaves and not enharmonic
            return this.octave.getNumberValue() - o.octave.getNumberValue();
        }
    }

    @Override
    public int hashCode() {
        // purposefully doesn't include index
        return this.pitchValue + duration.hashCode();
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set this note's Duration to the given duration.
     * Useful if this Note is partially constructed.
     *
     * @param d The duration to set.
     */
    public void setDuration(Duration d) {
        this.duration = d;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equalsIndex(BasicNote b) {
        return this.index == b.getIndex() && this.equals(b);
    }
}

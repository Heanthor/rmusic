package music;

/**
 * Holds notes used for testing.
 */
class NoteBank {
    public static Note cs = new Note(NoteValue.C, NoteValue.Type.SHARP, new Octave(4));
    public static Note db = new Note(NoteValue.D, NoteValue.Type.FLAT, new Octave(4));
    public static Note e = new Note(NoteValue.E, NoteValue.Type.NATURAL, new Octave(4));
    public static Note fb = new Note(NoteValue.F, NoteValue.Type.FLAT, new Octave(4));
    public static Note ab = new Note(NoteValue.A, NoteValue.Type.FLAT, new Octave(4));
    public static Note gs = new Note(NoteValue.G, NoteValue.Type.SHARP, new Octave(4));
    public static Note cs4 = new Note(NoteValue.C, NoteValue.Type.SHARP, new Octave(4));
    public static Note cs5 = new Note(NoteValue.C, NoteValue.Type.SHARP, new Octave(5));
    public static Note b4 = new Note(NoteValue.B, NoteValue.Type.NATURAL, new Octave(4));
    public static Note cb5 = new Note(NoteValue.C, NoteValue.Type.FLAT, new Octave(5));
    public static Note bs4 = new Note(NoteValue.B, NoteValue.Type.SHARP, new Octave(4));
    public static Note c5 = new Note(NoteValue.C, NoteValue.Type.NATURAL, new Octave(5));
    public static Note b3 = new Note(NoteValue.B, NoteValue.Type.NATURAL, new Octave(3));
    public static Note bs3 = new Note(NoteValue.B, NoteValue.Type.SHARP, new Octave(3));
    public static Note c4 = new Note(NoteValue.C, NoteValue.Type.NATURAL, new Octave(4));
    public static Note g3 = new Note(NoteValue.G, NoteValue.Type.NATURAL, new Octave(3));
    public static Note g5 = new Note(NoteValue.G, NoteValue.Type.NATURAL, new Octave(5));
    public static Note d3 = new Note(NoteValue.D, NoteValue.Type.NATURAL, new Octave(3));
    public static Note a4 = new Note(NoteValue.A, NoteValue.Type.NATURAL, new Octave(4));
}

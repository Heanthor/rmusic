package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MajorSharpKeys implements Key {
    C(new Note("C4")),
    G(new Note("G4")),
    D(new Note("D4")),
    A(new Note("A4")),
    E(new Note("E4")),
    B(new Note("B4")),
    Fs(new Note("F#4")),
    Cs(new Note("C#4"));

    private Note noteRepr;

    MajorSharpKeys(Note noteRepr) {
        this.noteRepr = noteRepr;
    }

    public Note getNoteRepr() {
        return noteRepr;
    }

    @Override
    public String toString() {
        return this.name() + " major";
    }
}

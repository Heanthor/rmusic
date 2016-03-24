package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MajorSharpKeys implements Key {
    C(new Note("C4:Q")),
    G(new Note("G4:Q")),
    D(new Note("D4:Q")),
    A(new Note("A4:Q")),
    E(new Note("E4:Q")),
    B(new Note("B4:Q")),
    Fs(new Note("F#4:Q")),
    Cs(new Note("C#4:Q"));

    private Note noteRepr;

    MajorSharpKeys(Note noteRepr) {
        this.noteRepr = noteRepr;
    }

    public Note getNoteRepr() {
        return noteRepr;
    }
}

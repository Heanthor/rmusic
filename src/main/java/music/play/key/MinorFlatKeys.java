package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MinorFlatKeys implements Key {
    A(new Note("A4:Q")),
    D(new Note("D4:Q")),
    G(new Note("G4:Q")),
    C(new Note("C4:Q")),
    F(new Note("F4:Q")),
    Bb(new Note("Bb4:Q")),
    Eb(new Note("Eb4:Q")),
    Ab(new Note("Ab4:Q"));

    private Note noteRepr;

    MinorFlatKeys(Note noteRepr) {
        this.noteRepr = noteRepr;
    }

    public Note getNoteRepr() {
        return noteRepr;
    }
}

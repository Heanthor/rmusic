package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MinorFlatKeys implements Key {
    A(new Note("A4")),
    D(new Note("D4")),
    G(new Note("G4")),
    C(new Note("C4")),
    F(new Note("F4")),
    Bb(new Note("Bb4")),
    Eb(new Note("Eb4")),
    Ab(new Note("Ab4"));

    private Note noteRepr;

    MinorFlatKeys(Note noteRepr) {
        this.noteRepr = noteRepr;
    }

    public Note getNoteRepr() {
        return noteRepr;
    }

    @Override
    public String toString() {
        return this.name() + " minor";
    }
}

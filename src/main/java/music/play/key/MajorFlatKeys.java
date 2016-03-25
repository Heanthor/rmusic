package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MajorFlatKeys implements Key {
    C(new Note("C4:Q")),
    F(new Note("F4:Q")),
    Bb(new Note("Bb4:Q")),
    Eb(new Note("Eb4:Q")),
    Ab(new Note("Ab4:Q")),
    Db(new Note("Db4:Q")),
    Gb(new Note("Gb4:Q")),
    Cb(new Note("Cb4:Q"));


    private Note noteRepr;

    MajorFlatKeys(Note noteRepr) {
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

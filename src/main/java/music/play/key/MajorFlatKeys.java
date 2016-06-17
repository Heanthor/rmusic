package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MajorFlatKeys implements Key {
    C(new Note("C4")),
    F(new Note("F4")),
    Bb(new Note("Bb4")),
    Eb(new Note("Eb4")),
    Ab(new Note("Ab4")),
    Db(new Note("Db4")),
    Gb(new Note("Gb4")),
    Cb(new Note("Cb4"));


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

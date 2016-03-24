package music.play.key;

import music.pitch.Note;

/**
 * @author reedt
 */
public enum MinorSharpKeys implements Key {
    A(new Note("A4:Q")),
    E(new Note("E4:Q")),
    B(new Note("B4:Q")),
    Fs(new Note("F#4:Q")),
    Cs(new Note("C#4:Q")),
    Gs(new Note("G#4:Q")),
    Ds(new Note("D#4:Q")),
    As(new Note("A#4:Q"));

    private Note noteRepr;

    MinorSharpKeys(Note noteRepr) {
        this.noteRepr = noteRepr;
    }

    public Note getNoteRepr() {
        return noteRepr;
    }
}

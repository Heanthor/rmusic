package music.play.key;

import music.pitch.Note;

/**
 * A key is a major or minor key signature, in sharps or flats.
 * Classes implementing this form groups of key signatures.
 * Note Cmaj/Am are listed under both sharp and flat enums.
 *
 * @author reedt
 */
public interface Key {
    Note getNoteRepr();

    default KeySignature getKeySignature() {
        return new KeySignature(this);
    }
}

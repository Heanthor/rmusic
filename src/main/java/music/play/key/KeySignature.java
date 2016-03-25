package music.play.key;

import music.pitch.Note;
import music.pitch.Octave;
import music.pitch.interval.IntervalUtils;
import music.pitch.interval.PerfectIntervals;
import music.rhythm.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Traverse the circle of fifths to find the sharps and flats for any major or minor key signature.
 * The notes in the generated list are the base notes that are sharped or flattened.
 * <p>
 * For example, the key signature for D major is represented as [F, C] instead of [F#, C#]
 *
 * @author reedt
 */
public class KeySignature {
    // Way more fun than hard coding lists!
    private final List<Note> keySignature;
    private final Key key;

    /**
     * Create a KeySignature of the given key.
     * @param key Key to determine key signature of
     */
    public KeySignature(Key key) {
        keySignature = determineKeySignature(key);
        this.key = key;
    }

    /**
     * Create a KeySignature from information about the key signature itself.
     * Useful for parsing MIDI file information into KeySignature format.
     * @param numAccidentals The number of sharps or flats in the key signature.
     * @param sharps If true, use sharps, otherwise flats.
     * @param major If true, use major, otherwise minor.
     */
    public KeySignature(int numAccidentals, boolean sharps, boolean major) {
        keySignature = determineKeySignature(numAccidentals, sharps);
        key = determineKey(numAccidentals, sharps, major);
    }

    public List<Note> getKeySignature() {
        return keySignature;
    }

    /**
     * Determines the list of sharps or flats in a given key signature.
     * <p>
     * For example, key of D major would return a list with F# and C#,
     * while D minor would return a list with Bb.
     *
     * @param keyIn Key to find the key signature of.
     * @return A list of sharps or flats present in the key signature.
     */
    private List<Note> determineKeySignature(Key keyIn) {
        // Normalize root note for comparison
        Note rootNote = normalizePitch(keyIn.getNoteRepr());
        ArrayList<Note> sig = new ArrayList<>();
        int numAccidentals = 0;

        // Sharp keys
        if (keyIn instanceof MajorSharpKeys ||
                keyIn instanceof MinorSharpKeys) {
            // Count number of sharps
            if (keyIn instanceof MajorSharpKeys) {
                // Major sharps
                for (MajorSharpKeys m : MajorSharpKeys.values()) {
                    if (m.getNoteRepr().equalsPitchOnly(rootNote)) {
                        break;
                    } else {
                        numAccidentals++;
                    }
                }
            } else {
                // Minor sharps
                for (MinorSharpKeys m : MinorSharpKeys.values()) {
                    if (m.getNoteRepr().equalsPitchOnly(rootNote)) {
                        break;
                    } else {
                        numAccidentals++;
                    }
                }
            }

            // Starting sharp
            Note tmp = new Note("F4:Q");

            // Populate key signature list
            for (int i = 0; i < numAccidentals; i++) {
                sig.add(tmp);

                // each sharp is a fifth above the last
                tmp = normalizePitch(IntervalUtils.getNoteAbove(tmp, PerfectIntervals.PERFECT_FIFTH));
            }

            return sig;
        } else {
            // Flat keys

            // Count number of flats
            if (keyIn instanceof MajorFlatKeys) {
                // Major flats
                for (MajorFlatKeys m : MajorFlatKeys.values()) {
                    if (m.getNoteRepr().equalsPitchOnly(rootNote)) {
                        break;
                    } else {
                        numAccidentals++;
                    }
                }
            } else {
                // Minor flats
                for (MinorFlatKeys m : MinorFlatKeys.values()) {
                    if (m.getNoteRepr().equalsPitchOnly(rootNote)) {
                        break;
                    } else {
                        numAccidentals++;
                    }
                }
            }

            // Starting flat
            Note tmp = new Note("B4:Q");

            // Populate key signature list
            for (int i = 0; i < numAccidentals; i++) {
                sig.add(tmp);

                // each flat is a fifth below the last
                tmp = normalizePitch(IntervalUtils.getNoteBelow(tmp, PerfectIntervals.PERFECT_FIFTH));
            }

            return sig;
        }
    }

    /**
     * Finds list of sharps or flats described by the parameters. This list is independent of major or minor.
     * @param numAccidentals Number of sharps or flats.
     * @param sharps True for sharps, flats otherwise.
     * @return A list of notes that represents the key signature.
     */
    private List<Note> determineKeySignature(int numAccidentals, boolean sharps) {
        List<Note> sig = new ArrayList<>();
        if (sharps) {
            // Starting sharp
            Note tmp = new Note("F4:Q");

            // Populate key signature list
            for (int i = 0; i < numAccidentals; i++) {
                sig.add(tmp);

                // each sharp is a fifth above the last
                tmp = normalizePitch(IntervalUtils.getNoteAbove(tmp, PerfectIntervals.PERFECT_FIFTH));
            }

            return sig;
        } else {
            // Starting flat
            Note tmp = new Note("B4:Q");

            // Populate key signature list
            for (int i = 0; i < numAccidentals; i++) {
                sig.add(tmp);

                // each flat is a fifth below the last
                tmp = normalizePitch(IntervalUtils.getNoteBelow(tmp, PerfectIntervals.PERFECT_FIFTH));
            }

            return sig;
        }
    }

    /**
     * Get the Key object associated with the key signature described by the parameters.
     * @param numAccidentals The number of sharps or flats in the key signature.
     * @param sharps True for sharps, flats otherwise.
     * @param major True for major, minor otherwise.
     * @return The major or minor key with numAccidentals number of sharps or flats in its key signature.
     */
    private Key determineKey(int numAccidentals, boolean sharps, boolean major) {
        if (sharps) {
            if (major) {
                return MajorSharpKeys.values()[numAccidentals];
            } else {
                return MinorSharpKeys.values()[numAccidentals];
            }
        } else {
            if (major) {
                return MajorFlatKeys.values()[numAccidentals];
            } else {
                return MinorFlatKeys.values()[numAccidentals];
            }
        }
    }

    /**
     * @return noteIn Note with an octave of 4, and a duration of quarter note.
     */
    private Note normalizePitch(Note noteIn) {
        return new Note(noteIn.basePitch, noteIn.accidental, Octave.FOUR, new Duration(Duration.DurationValue.QUARTER, false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeySignature that = (KeySignature) o;

        return keySignature.equals(that.keySignature);
    }

    @Override
    public int hashCode() {
        return keySignature.hashCode();
    }

    public Key getKey() {
        return key;
    }
}

package music.play;

import static music.rhythm.Duration.DurationValue;

/**
 * Mutable class representing tempo. As the tempo can change during a piece of music,
 * this class can change to represent this.
 *
 * @author reedt
 */
public class Tempo {
    public DurationValue note;
    public int bpm;

    /**
     * Creates a Tempo object.
     * This represents the modern music notation "[note] = [beats per minute]"
     * @param note
     * @param bpm
     */
    public Tempo(DurationValue note, int bpm) {
        this.note = note;
        this.bpm = bpm;
    }
}

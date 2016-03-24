package music.play;

import static music.rhythm.Duration.DurationValue;

/**
 * Created by reedtrevelyan on 3/23/16.
 */
public class Tempo {
    public final DurationValue note;
    public final int bpm;

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

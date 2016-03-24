package music.pitch.interval;

/**
 * Perfect intervals are special, consonance forming intervals.
 * @author reedt
 */
public enum PerfectIntervals implements Interval {
    PERFECT_UNISON(0),
    PERFECT_FOURTH(5),
    PERFECT_FIFTH(7),
    OCTAVE(12);

    private int numHalfSteps;
    PerfectIntervals(int numHalfSteps) {
        this.numHalfSteps = numHalfSteps;
    }

    public int getNumHalfSteps() {
        return numHalfSteps;
    }
}

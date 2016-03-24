package music.pitch.interval;

/**
 * @author reedt
 */
public enum AugmentedIntervals implements Interval {
    _2nd(3),
    _3rd(5),
    _4th(6),
    _5th(8),
    _6th(10),
    _7th(12);

    private int numHalfSteps;
    AugmentedIntervals(int numHalfSteps) {
        this.numHalfSteps = numHalfSteps;
    }

    public int getNumHalfSteps() {
        return numHalfSteps;
    }
}

package music.pitch.interval;

/**
 * @author reedt
 */
public enum MajorIntervals implements Interval {
    _2nd(2),
    _3rd(4),
    _6th(9),
    _7th(11);

    private int numHalfSteps;
    MajorIntervals(int numHalfSteps) {
        this.numHalfSteps = numHalfSteps;
    }

    public int getNumHalfSteps() {
        return numHalfSteps;
    }
}

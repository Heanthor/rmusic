package music.pitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Multiple Notes sounded at once.
 * A Chord is just a collection of Note objects.
 * @author Reed
 */
public class Chord {
    public final ArrayList<Note> chord = new ArrayList<Note>();

    public Chord(Note... notes) {
        Collections.addAll(chord, notes);
    }

    public String toString() {
        ArrayList<String> sortedList = new ArrayList<String>(chord.size());

        sortedList.addAll(chord.stream().map(Note::pitchOnlyToString).collect(Collectors.toList()));

        sortedList.sort(null); // Notes are Comparable, so no Comparator is necessary
        return String.join("-", sortedList);
    }
}

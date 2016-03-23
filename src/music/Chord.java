package music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by reedtrevelyan on 3/22/16.
 */
public class Chord {
    public final List<Note> chord = new ArrayList<Note>();

    public Chord(Note... notes) {
        Collections.addAll(chord, notes);
    }

    public String toString() {
        List<String> sortedList = new ArrayList<String>(chord.size());

        for (Note n: chord) {
            sortedList.add(n.toString());
        }

        sortedList.sort(null); // Notes are Comparable
        return String.join("-", sortedList);
    }
}

package music.play;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.play.key.Key;
import music.play.key.KeySignature;
import music.rhythm.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author reedt
 */
public class Staff {
    public final Tempo tempo;
    public final KeySignature keySignature;
    public final TimeSignature timeSignature;
    public final List<Voice> voices;

    public Staff(Tempo tempo, Key key, TimeSignature timeSignature, List<Voice> voices) {
        this.tempo = tempo;
        this.voices = voices;
        this.keySignature = key.getKeySignature();
        this.timeSignature = timeSignature;
    }

    public Staff(Tempo tempo, Key key, TimeSignature timeSignature, Voice[] voices) {
        this.tempo = tempo;
        this.voices = Arrays.asList(voices);
        this.keySignature = key.getKeySignature();
        this.timeSignature = timeSignature;
    }

    public Staff(Tempo tempo, TimeSignature ts, Voice[] voices) {
        this.tempo = tempo;
        this.voices = Arrays.asList(voices);
        keySignature = null;
        timeSignature = ts;
    }

    public Staff(Tempo tempo, TimeSignature ts, List<Voice> voices) {
        this.tempo = tempo;
        this.voices = voices;
        keySignature = null;
        timeSignature = ts;
    }

    @Override
    public String toString() {
        String kString = keySignature == null ? "n/a" : keySignature.toString();

        String staffInfo = String.format("Staff: Tempo: %d bpm, Key %s, Time Signature %d/%d\n",
                tempo.bpm, kString, timeSignature.getNumerator(), timeSignature.getDenominator());
        StringBuilder voiceString = new StringBuilder();
        for (Voice v : voices) {
            voiceString.append(v.toString()).append("\n");
        }

        return staffInfo + "\n" + voiceString;
    }

    public String getAlignedStaff() {
        ArrayList<StringBuilder> lines = new ArrayList<>();
        // init lines
        lines.addAll(voices.stream().
                map(voice -> new StringBuilder(voice.getNote(0).toString())).collect(Collectors.toList()));

        // init to 0 for each voice
        ArrayList<Integer> posPointers = voices.stream().
                map(v -> 0).collect(Collectors.toCollection(ArrayList::new));

        Duration minResolution = new Duration(Duration.DurationValue.WHOLE, false);

        for (int i = 0; i < voices.size(); i++) {
            Voice v = voices.get(i);
            // pull the note at each pointer's location
            BasicNote n = v.getNote(posPointers.get(i));

            if (n.getDuration().compareTo(minResolution) < 0) {
                minResolution = n.getDuration();
            }
        }

        // min resolution gets incremented, others do not
        for (int i = 0; i < voices.size(); i++) {
            Voice v = voices.get(i);
            BasicNote n = v.getNote(posPointers.get(i));

            if (n.getDuration().equals(minResolution)) {
                lines.get(i).append(n.toString());
            } else {
                lines.get(i).append("...");
            }
        }

        StringBuilder sb = new StringBuilder();

        for (StringBuilder line: lines) {
            sb.append(line).append("\n");
        }

        //TODO
        return sb + "\n";
    }
}

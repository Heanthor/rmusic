package display;

import music.pitch.BasicNote;
import music.pitch.Note;
import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import music.play.Voice;
import music.play.key.MajorSharpKeys;
import music.rhythm.Rest;

import javax.swing.*;
import java.util.ArrayList;

public class StaffViewer {
    public static void main(String[] args) {
        JFrame f = new JFrame("Staff Viewer");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocation(50, 50);
        f.setSize(1440, 900);

        ArrayList<BasicNote> rh = new ArrayList<>();
        rh.add(new Note("C5:H"));
        rh.add(new Note("E5:Q"));
        rh.add(new Note("G5:Q"));
        rh.add(new Note("B4:Qd"));
        rh.add(new Note("C5:S"));
        rh.add(new Note("D5:S"));
        rh.add(new Note("C5:Q"));
        rh.add(Rest.parseRestString("R:Q"));

        rh.add(new Note("A5:H"));
        rh.add(new Note("G5:Q"));
        rh.add(new Note("C6:Q"));
        rh.add(new Note("G5:Q"));
        rh.add(new Note("F5:T"));
        rh.add(new Note("G5:T"));
        rh.add(new Note("F5:T"));
        rh.add(new Note("G5:T"));
        rh.add(new Note("E5:S"));
        rh.add(new Note("F5:S"));
        rh.add(new Note("E5:H"));

        ArrayList<BasicNote> lh = new ArrayList<>();
        lh.add(new Note("C4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("E4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("C4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("E4:E"));
        lh.add(new Note("G4:E"));

        lh.add(new Note("D4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("F4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("C4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("E4:E"));
        lh.add(new Note("G4:E"));

        lh.add(new Note("C4:E"));
        lh.add(new Note("A4:E"));
        lh.add(new Note("F4:E"));
        lh.add(new Note("A4:E"));
        lh.add(new Note("C4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("E4:E"));
        lh.add(new Note("G4:E"));

        lh.add(new Note("B4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("D4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("C4:E"));
        lh.add(new Note("G4:E"));
        lh.add(new Note("E4:E"));
        lh.add(new Note("G4:E"));

        Tempo t = new Tempo(Tempo.CommonTempos.ALLEGRO);
        Staff mozartK545 = new Staff(t,
                MajorSharpKeys.C,
                new TimeSignature(4, TimeSignature.DenominatorChoices._4),
                new Voice[]{new Voice(rh), new Voice(lh)});

        StaffView sv = new StaffView(mozartK545);

        f.getContentPane().add(sv);
        f.setVisible(true);
    }
}

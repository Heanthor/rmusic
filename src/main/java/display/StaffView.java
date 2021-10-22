package display;

import music.pitch.BasicNote;
import music.play.Staff;
import music.play.Voice;
import music.rhythm.Rest;

import javax.swing.*;
import java.awt.*;

public class StaffView extends JComponent {
    private Staff staff;
    private final int BEAT_WIDTH_PX = 100;
    private final int VOICE_HEIGHT_PX = 40;
    private final Color[] VOICE_COLORS = new Color[]{
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.ORANGE,
            Color.PINK
    };

    public StaffView(Staff staff) {
        this.staff = staff;

        this.setPreferredSize(new Dimension(500, 700));

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int vOffset = 0;
        for (Voice v : staff.voices) {
            int nOffset = 0;

            for (BasicNote n : v.melody) {
                int vSpacing = 0;

                if (vOffset > 0) {
                    vSpacing = 5;
                }

                int noteWidth = (int)(n.getDuration().getDoubleValue() * BEAT_WIDTH_PX);

                g.setColor(VOICE_COLORS[vOffset]);
                if (!(n instanceof Rest)) {
                    g.fillRect(nOffset, vOffset * VOICE_HEIGHT_PX + vSpacing, noteWidth, VOICE_HEIGHT_PX);
                }
                nOffset += noteWidth + 1;
            }
            vOffset++;
        }
    }
}

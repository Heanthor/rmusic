package music.play;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author reedt
 */
public class VoiceTest {
    @Test
    public void parseVoiceString() throws Exception {
        String voiceString = "A4:Q,B4:Q,R:Q,C4:W";

        Voice v = new Voice(voiceString);

        assertEquals("Voice: A4:Q, B4:Q, R:Q, C4:W", v.toString());

        voiceString = "A3:Qd,C4:H,E6:Hd";

        assertEquals("Voice: A3:Qd, C4:H, E6:Hd", new Voice(voiceString).toString());
    }
}
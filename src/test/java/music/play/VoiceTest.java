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

        assertEquals("Voice 0: A4:Q...B4:Q...R:Q....C4:W", v.toString());

        voiceString = "A3:Qd,C4:H,E6:Hd";

        assertEquals("Voice 0: A3:Qd..C4:H...E6:Hd", new Voice(voiceString).toString());
    }

    @Test
    public void testDurationValue() {
        String voiceString = "A4:S,B4:S,R:S,C4:S";
        Voice v = new Voice(voiceString);

        assertTrue(0.25 == v.getTotalDurationValue());

        String voiceString2 = "A4:S,B4:S,R:E,C4:S";
        v = new Voice(voiceString2);

        assertTrue(((double)5 / (double)16) == v.getTotalDurationValue());
    }
}
package util.xml;

import music.play.Staff;
import music.play.Tempo;
import music.play.TimeSignature;
import org.w3c.dom.Element;

/**
 * Created by Reed.t on 6/17/16.
 */
public class NoteXMLParser {
    String file;

    public NoteXMLParser(String file) {
        this.file = file;
    }

    public Staff parse() {
        // tempo, key, time signature, voice array
        Element rootElement = XMLParserHelper.getRootElement(file);

        Tempo tempo = new Tempo(Integer.parseInt(XMLParserHelper.getContent(rootElement, "Tempo")));
        TimeSignature ts = new TimeSignature(XMLParserHelper.getContent(rootElement, "TimeSignature"));
        return null;
    }

}

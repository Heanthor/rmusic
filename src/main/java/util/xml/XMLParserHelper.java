package util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


/**
 * This class provides some small helper methods for XML parsing.
 * From MusicalNeutrino on github
 */
public class XMLParserHelper {
    private XMLParserHelper() {
    }

    /**
     * Given the path to an XML file, open the file and return the XML
     * root element
     *
     * @param filename path to an XML file
     * @return root element of the XML document
     */
    public static Element getRootElement(String filename) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom = null;

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(filename);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException("Could not open file " + filename);
        }

        return dom.getDocumentElement();
    }

    /**
     * Get the value of an element contained in an XML element. The tag must
     * appear only once.
     *
     * @param ele higher level XML element that contains the desired element
     * @param tag tag of the element in the higher level element
     * @return element with the given tag
     */
    public static Element getSingleElement(Element ele, String tag) {
        NodeList nlist = ele.getElementsByTagName(tag);
        if (nlist.getLength() != 1)
            throw new RuntimeException(String.format("Number of elements with tag '%s' should be (1), found %d.", tag, nlist.getLength()));
        return (Element) nlist.item(0);
    }

    /**
     * Helper method to quickly get String content
     *
     * @param e   element containing the tag with the string content
     * @param tag XML tag of the element containing the string content
     * @return string in the XML element
     */
    public static String getContent(Element e, String tag) {
        NodeList nl = e.getElementsByTagName(tag);
        if (nl.getLength() != 1)
            throw new RuntimeException(String.format("Expecting only a single tag with tag name: '%s'. Node list length is %d",
                    tag, nl.getLength()));

        return nl.item(0).getTextContent();
    }

    /**
     * Helper method to quickly get Integer content
     *
     * @param e   element containing the tag with the string content
     * @param tag XML tag of the element containing the string content
     * @return integer in the XML element
     */
    public static int getContentInteger(Element e, String tag) {
        return Integer.parseInt(getContent(e, tag));
    }
}

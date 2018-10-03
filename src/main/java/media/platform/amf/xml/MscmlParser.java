package media.platform.amf.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import media.platform.amf.core.xml.XmlParser;

public class MscmlParser {

    public boolean parse(String xml) {
        if (xml == null) {
            return false;
        }

        XmlParser xmlParser = new XmlParser();
        Document doc = xmlParser.parse(xml);

        if (doc == null) {
            return false;
        }


        // "xml": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n
        // <MediaServerControl>\r\n
        // <request>\r\n
        // <playcollect maxdigits=\"4\">\r\n
        // <prompt>\r\n
        // <audio url=\"file://TAS/password_input.alaw\"/>\r\n
        // </prompt>\r\n</playcollect>\r\n
        // </request>\r\n
        // </MediaSer verControl>\r\n"

        NodeList nl = doc.getElementsByTagName("MediaServiceControl");
        if (nl == null) {
            // Not MSCML
            return false;
        }

        Node node = nl.item(0).getFirstChild();
        if (node == null) {
            return false;
        }

        //node.getNodeName();
//        node.getNodeValue();

        return true;
    }
}

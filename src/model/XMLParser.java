package model;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;

public class XMLParser {
    public static HashMap decodeXML(String xml_path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        String xml_properties_tag_name = "chunk";
        HashMap<String, HashMap<String, String>> xml_properties = new HashMap();

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        Document doc = null;

        try {
            doc = db.parse(new File(xml_path));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }

        doc.getDocumentElement().normalize();
        NodeList properties = doc.getElementsByTagName(xml_properties_tag_name);

        for (int i = 0; i < properties.getLength(); i++) {
            Node property = properties.item(i);
            Element element = (Element) property;
            xml_properties.putAll(getElementAttributes(element));
        }

        return xml_properties;
    }

    private static HashMap<String, HashMap<String, String>> getElementAttributes(Element element) {
        String name, type, format, node;

        if (element.getElementsByTagName("name").getLength() > 0) {
            name = element.getElementsByTagName("name").item(0).getTextContent();
        } else {
            name = "N/A";
        }

        if (element.getElementsByTagName("type").getLength() > 0) {
            type = element.getElementsByTagName("type").item(0).getTextContent();
        } else {
            type = "N/A";
        }

        if (element.getElementsByTagName("format").getLength() > 0) {
            format = element.getElementsByTagName("format").item(0).getTextContent();
        } else {
            format = "N/A";
        }

        if (element.getElementsByTagName("node").getLength() > 0) {
            node = element.getElementsByTagName("node").item(0).getTextContent();
        } else {
            node = "N/A";
        }

        String finalName = name;
        String finalType = type;
        String finalFormat = format;
        String finalNode = node;

        HashMap<String, String> innerHashmap = new HashMap<>();
        innerHashmap.put("name", finalName);
        innerHashmap.put("type", finalType);
        innerHashmap.put("format", finalFormat);

        HashMap<String, HashMap<String, String>> outerHashmap = new HashMap<String, HashMap<String, String>>();
        outerHashmap.put(finalNode, innerHashmap);
        return outerHashmap;
    }
}

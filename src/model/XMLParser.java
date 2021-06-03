package model;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLParser {
    public static HashMap decodeXML(String xml_path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        HashMap<String, XmlProperty> xml_properties = new HashMap<String, XmlProperty>();
        String xml_root_element_tag = "config";
        String xml_min_element_tag = "min";
        String xml_max_element_tag = "max";
        String xml_index_element_tag = "index";
        DocumentBuilder db = null;
        Document doc = null;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        try {
            doc = db.parse(new File(xml_path));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }

        doc.getDocumentElement().normalize();
        NodeList entries = doc.getElementsByTagName("*");
        int first_depth_property_counter = 0;

        for (int i=0; i<entries.getLength(); i++) {
            Element element = (Element) entries.item(i);

            if (!element.getNodeName().equals(xml_root_element_tag) && getElementDepth(element) == 1) {
                XmlProperty prop = new XmlProperty(
                        Double.parseDouble(element.getElementsByTagName(xml_min_element_tag).item(0).getTextContent()),
                        Double.parseDouble(element.getElementsByTagName(xml_max_element_tag).item(0).getTextContent()),
                        Integer.parseInt(element.getElementsByTagName(xml_index_element_tag).item(0).getTextContent())
                );

                xml_properties.put(element.getNodeName(), prop);
                first_depth_property_counter++;
            }
        }

        return xml_properties;
    }

    public static int getElementDepth(Element element) {
        Node parent = element.getParentNode();
        int depth = 0;

        while (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
            depth++;
            parent = parent.getParentNode();
        }

        return depth;
    }

    public static class XmlProperty {
        private double min;
        private double max;
        private int index;

        XmlProperty(double min, double max, int index) {
            this.min = min;
            this.max = max;
            this.index = index;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void print() {
            System.out.println("min: " + min);
            System.out.println("max: " + min);
            System.out.println("index: " + index);
        }
    }
}
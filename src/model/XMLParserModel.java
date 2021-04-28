package model;

import java.util.HashMap;
import java.util.Observable;

public class XMLParserModel extends Observable {
private HashMap hashMap;

    public HashMap getHashMap() {
        return hashMap;
    }
    public void ParseXML(String path)
    {
        hashMap = XMLParser.decodeXML(path);
        setChanged();
        notifyObservers();
    }
}

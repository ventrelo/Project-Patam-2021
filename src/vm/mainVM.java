package vm;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.XMLParserModel;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class mainVM extends Observable implements Observer {
   public XMLParserModel xmlParserModel = new XMLParserModel();
   public HashMap xmlSettings;

  public mainVM(XMLParserModel xmlPM)
  {
     this.xmlParserModel = xmlPM;
  }
   public void parseXML(String path)
   {
      xmlParserModel.ParseXML(path);
   }
   @Override
   public void update(Observable o, Object arg) {
      if(o==xmlParserModel)
      {
         xmlSettings = xmlParserModel.getHashMap();
      }
   }
}

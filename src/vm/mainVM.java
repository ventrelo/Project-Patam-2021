package vm;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.TimeSeries.TimeSeries;
import model.XMLParserModel;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class mainVM extends Observable implements Observer {
   public XMLParserModel xmlParserModel = new XMLParserModel();
   public HashMap xmlSettings;
   private model.playable playable;

    //Joystick properties
    public DoubleProperty aileron;
    public DoubleProperty elevator;
    public DoubleProperty rudder;
    public DoubleProperty throttle;

    //Clock Board properties
    public DoubleProperty height;
    public DoubleProperty speed;
    public DoubleProperty direction;
    public DoubleProperty roll;
    public DoubleProperty pitch;
    public DoubleProperty yaw;
    //PlayBack properties
    public DoubleProperty playback_speed;

  public mainVM(XMLParserModel xmlPM)
  {
     this.xmlParserModel = xmlPM;
     aileron= new SimpleDoubleProperty();
     elevator= new SimpleDoubleProperty();
     rudder= new SimpleDoubleProperty();
     throttle= new SimpleDoubleProperty();
     height = new SimpleDoubleProperty();
     speed = new SimpleDoubleProperty();
     direction = new SimpleDoubleProperty();
     roll = new SimpleDoubleProperty();
     pitch = new SimpleDoubleProperty();
     yaw = new SimpleDoubleProperty();
     //
     playback_speed = new SimpleDoubleProperty();
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
      if(o == null) //This will be a future time series object
      {
          aileron = null;
          elevator = null;
          rudder = null;
          throttle = null;
          height = null;
          speed = null;
          direction = null;
          roll = null;
          pitch= null;
          yaw = null;
          setChanged();
          notifyObservers();

      }
   }

    public void pause() {
    }

    public void play() {
    }
}

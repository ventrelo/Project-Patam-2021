package vm;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import model.XMLParserModel;
import model.playableTS;

import java.util.*;

public class mainVM extends Observable implements Observer {
   public XMLParserModel xmlParserModel;
   public HashMap xmlSettings;
   public model.playableTS playable;
   private Timer t = new Timer();
   //PlayBack properties
   public DoubleProperty playback_speed;


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
      t.cancel();
    }

    public void play() {
      t.cancel();
      if(playable != null) {
          TimeTaskPlay TTP = new TimeTaskPlay();
          TTP.setPTS(playable);
          t.scheduleAtFixedRate(TTP,0, (long) (1000/playback_speed.getValue()));
      }
    }

    public playableTS getPlayable() {
        return playable;
    }
}
class TimeTaskPlay extends TimerTask {
    playableTS PTS;

    public void setPTS(playableTS PTS) {
        this.PTS = PTS;
    }

    @Override
    public void run() {
        PTS.play();
    }
}

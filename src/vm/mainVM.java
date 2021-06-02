package vm;



import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.FlightSim;
import model.TimeSeries.TimeSeriesAnomalyDetector;
import model.XMLParserModel;
import model.playableTS;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class mainVM extends Observable implements Observer {
    public XMLParserModel xmlParserModel;
    public HashMap xmlSettings;
    public model.playableTS playable;
    public Timer t;
    public TimeSeriesAnomalyDetector detector;
    public FlightSim flightSim;
   //PlayBack properties
    public DoubleProperty playback_speed;
    public StringProperty playback_time;
    public DoubleProperty playback_frame;

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
      flightSim = new FlightSim();
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
     playback_time = new SimpleStringProperty("00:00:00");
     playback_frame = new SimpleDoubleProperty();


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
      if(o==playable)
      {
          Float [] value = playable.values;
          //playable.values

          Platform.runLater(() -> {
              playback_time.setValue(ConvertTime(playback_frame.getValue()));
              aileron.setValue(value[0]);
              elevator.setValue(value[1]);
              rudder.setValue(value[2]);
              throttle.setValue(value[6]);
              height.setValue(value[25]);
              speed.setValue(value[24]);
              direction.setValue(value[36]);
              roll.setValue(value[28]);
              pitch.setValue(value[29]);
              yaw.setValue(value[20]);
              setChanged();
              notifyObservers();
          });


      }
   }

    public void pause(){
        if(t!=null)
            t.cancel();
        
    }

    public void play() {

      if(t!=null)
          t.cancel();
      this.t = new Timer();
      if(playable != null) {
          TimerTask TTP = new TimeTaskPlay(playable);
          t.scheduleAtFixedRate(TTP,0, (long) (1000/playback_speed.getValue()));

      }
    }

    public playableTS getPlayable() {
        return playable;
    }
    public void setPlayable(String path)
    {
        //Handle the time series
        playable = new playableTS();
        playback_frame.setValue(0);
        playback_time.setValue(ConvertTime(playback_frame.getValue()));
        playable.setTimeSeries(path);
        playable.addObserver(this);

        //Handle the Flight gear
        flightSim.openSocket();
        flightSim.setValues(path);
    }
    class TimeTaskPlay extends TimerTask {
        playableTS PTS;
        public TimeTaskPlay(playableTS playableTS) {
            this.PTS = playableTS;
        }
        @Override
        public void run() {
            int frame = playback_frame.getValue().intValue();

            if(frame > playable.MaxFrame) {
                pause();
                playback_frame.setValue(0);
            }
            else {

                PTS.play(frame);
                flightSim.play(frame);
                frame++;
                playback_frame.setValue(frame);
            }



        }
    }
    public String ConvertTime(double seconds)
    {
        String hour,min,sec;
      int hours = (int) (seconds/3600);
      if(hours<=9)
          hour = new StringBuilder().append("0").append(String.valueOf(hours)).toString();
      else
          hour= String.valueOf(hours);
      int mins = (int) ((seconds%3600)/60)%60;
        if(mins<=9)
            min = new StringBuilder().append("0").append(String.valueOf(mins)).toString();
        else
            min= String.valueOf(mins);
      int secs = (int) (seconds%60);

        if(secs<=9)
            sec = new StringBuilder().append("0").append(String.valueOf(secs)).toString();
        else
            sec= String.valueOf(secs);
      return hour+':'+min+':'+sec;

    }
    public void SetDetector(String path, String nameClass)
    {
        try{
            URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {
                    new URL("file://"+path)
            });
            System.out.println("file://"+path);
            System.out.println(nameClass);
            Class<?> c=urlClassLoader.loadClass(nameClass);
            detector=(TimeSeriesAnomalyDetector) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



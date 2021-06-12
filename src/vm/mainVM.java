package vm;



import Algorithms.SimpleAnomalyDetector;
import Algorithms.ZScore;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import model.FlightSim;
import model.TimeSeries.*;
import model.XMLParser;
import model.XMLParserModel;
import model.playableTS;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class mainVM extends Observable implements Observer {
    public XMLParserModel xmlParserModel;
    public HashMap<String, XMLParser.XmlProperty> xmlSettings;
    public model.playableTS playable;
    public Timer t;
    public TimeSeriesAnomalyDetector detector;
    SimpleAnomalyDetector helper = new SimpleAnomalyDetector();
    public FlightSim flightSim;
    public   boolean showAlarm = false;
    HashMap<Number,String> anomalyReports;
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
              checkIfAlarm();
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

    public void fillSeriesT(XYChart.Series series, String str) {
      int index = xmlSettings.get(str).getIndex();
      List<Float[]> vals =playable.timeSeries.getValues();
      series.getData().clear();
      for(int i=0 ;i < playback_frame.getValue();i++)
      {
          //if(i%10==0)
            series.getData().add(new XYChart.Data(i,vals.get(i)[index]));

      }
    }

    public void learnN() {

      if(detector != null && playable.timeSeries != null && playable != null)
      {
          helper.learnNormal(playable.timeSeries);
          detector.learnNormal(playable.timeSeries);
      }
    }
    public void detect() {
        if(detector != null && playable.timeSeries != null)
        {
            anomalyReports = new HashMap<Number, String>();
            List<AnomalyReport> anomalyReportList = detector.detect(playable.timeSeries);
            for(AnomalyReport ano : anomalyReportList)
            {
                anomalyReports.put(ano.timeStep,ano.description);
            }

        }
    }

    public void fillSeriesC(XYChart.Series series, String str) {
        int indexMain = xmlSettings.get(str).getIndex();
        int indexCor = -1;
        String fetur1 = playable.timeSeries.getHeaders().get(indexMain);
        String fetur2 = null;

        for(CorrelatedFeatures cor : helper.getNormalModel())
        {
            if(fetur1.equals(cor.feature1))
            {
                fetur2 = cor.feature2;
                indexCor++;
                break;
            }


        }
        if (fetur2 != null) {


            for (String string : playable.timeSeries.getHeaders()) {
                if (fetur2.equals(string)) {
                    break;
                } else indexCor++;
            }

            List<Float[]> vals = playable.timeSeries.getValues();
            series.getData().clear();
            if (indexCor != -1)
                for (int i = 0; i < playback_frame.getValue(); i++) {
                    //if(i%10==0)
                    series.getData().add(new XYChart.Data(vals.get(i)[indexMain],vals.get(i)[indexCor]));

                }
        }
    }
    public void fillAnoSeries(LineChart<Number, Number> graph, String str) {
        graph.getData().clear();
        XYChart.Series line = new XYChart.Series();
        XYChart.Series anomalies = new XYChart.Series();
        List<Float[]> vals = playable.timeSeries.getValues();
        Line line1=null;
        int indexMain = xmlSettings.get(str).getIndex();
        String fetur1 = playable.timeSeries.getHeaders().get(indexMain);
        if(detector.getClass() == helper.getClass())
        {
            SimpleAnomalyDetector AnoDetect = (SimpleAnomalyDetector) detector;
            for(CorrelatedFeatures cor : AnoDetect.getNormalModel())
            {
                if(fetur1.equals(cor.feature1))
                {
                    line1 = cor.lin_reg;
                    break;
                }


            }
            float val;
            for(int i=0;i<playback_frame.getValue();i++)
            {
                val = vals.get(i)[indexMain];
                line.getData().add(new XYChart.Data(val,line1.f(val)));

            }
            graph.getData().add(line);
            for(int i=0;i<playback_frame.getValue();i++)
            {
                val = vals.get(i)[indexMain];
                if(anomalyReports.containsKey((long)i)) {
                    anomalies.getData().add(new XYChart.Data(i, val));
                }

            }
            graph.getData().add(anomalies);

        }

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
            Class<?> c=urlClassLoader.loadClass(nameClass);
            detector=(TimeSeriesAnomalyDetector) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkIfAlarm()
    {
        if(anomalyReports != null)
        {
            if(anomalyReports.containsKey(playback_frame))
            {
                showAlarm = true;
            } else
                showAlarm = false;
        }
    }

}



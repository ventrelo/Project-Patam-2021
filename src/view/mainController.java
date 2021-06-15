package view;




import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.XMLParser;
import view.joystick.Joystick;
import vm.mainVM;

import javax.swing.text.LabelView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class mainController implements Observer {

    @FXML
    VBox cb_graphs,cb_props,cb_vals,indicators;
    @FXML
    AnchorPane playback_bar;
    @FXML
    Slider js_playback_bar;
    @FXML
    Joystick joystick;
    @FXML
    Label height,speed,direction,roll,pitch,yaw;
    @FXML
    Label pb_speed, js_vid_time;
    @FXML
    LineChart<Number,Number> graph1,graph2,graph3;
    @FXML
    NumberAxis xAxisT,yAxisT,xAxisC,yAxisC;
    @FXML
    Rectangle indi1, indi2, indi3,indi4;

    DoubleProperty pb_speed_d = new SimpleDoubleProperty(1.00);
    boolean updateGraphs = false;
    String str = null;



    public vm.mainVM mainVM;
    public void setMainVM(mainVM vm)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(5);
        this.mainVM = vm;
        mainVM.parseXML("./Assets/xmlSettings.xml");
        indi1.setFill(Color.GREEN);
        // Clock Board labels binding
        height.textProperty().bind(mainVM.height.asString("%.4f"));
        speed.textProperty().bind(mainVM.speed.asString("%.4f"));
        direction.textProperty().bind(mainVM.direction.asString("%.4f"));
        roll.textProperty().bind(mainVM.roll.asString("%.4f"));
        pitch.textProperty().bind(mainVM.pitch.asString("%.4f"));
        yaw.textProperty().bind(mainVM.yaw.asString("%.4f"));

        //Play Back bar label binding
        pb_speed.textProperty().bind(pb_speed_d.asString());
        mainVM.playback_speed.bind(pb_speed_d);
        js_playback_bar.valueProperty().bindBidirectional(mainVM.playback_frame);
        js_vid_time.textProperty().bind(mainVM.playback_time);

    }
    public void init()
    {

    }
    public void uploadXMLsett(ActionEvent event) {

        Stage stage = new Stage();
        FileChooser fil_chooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fil_chooser.setInitialDirectory(new File(currentPath));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML file (*.xml)","*.xml");
        fil_chooser.getExtensionFilters().add(extensionFilter);
        File file = fil_chooser.showOpenDialog(stage);
        Alert alert;
        if (file != null) {

            mainVM.parseXML(file.getAbsolutePath());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("XML settings upload successful");
            indi1.setFill(Color.GREEN);

        } else
        {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("XML settings upload FAILED");
            indi1.setFill(Color.BLACK);
        }
        alert.showAndWait();
    }
    public void uploadCSVsett(ActionEvent event) {

        Stage stage = new Stage();
        FileChooser fil_chooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fil_chooser.setInitialDirectory(new File(currentPath));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV file (*.csv)","*.csv");
        fil_chooser.getExtensionFilters().add(extensionFilter);
        File file = fil_chooser.showOpenDialog(stage);
        Alert alert;
        if (file != null) {
            mainVM.setPlayable(file.getAbsolutePath());
            js_playback_bar.maxProperty().setValue(mainVM.playable.MaxFrame);
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("CSV  upload successful");
            indi2.setFill(Color.GREEN);
            indi4.setFill(Color.BLACK);
        } else
        {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("CSV upload FAILED");
            indi2.setFill(Color.BLACK);
            indi4.setFill(Color.BLACK);
        }
        alert.showAndWait();
    }
    public void uploadDetectorPath(ActionEvent event)
    {
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File file = directoryChooser.showDialog(stage);
        Alert alert = null;
        if (file != null) {
            TextInputDialog dialog = new TextInputDialog("Enter the class name");
            dialog.setTitle("Class name input");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                mainVM.SetDetector(file.getAbsolutePath(), result.get());
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Anomaly Detector upload successful");
            }
        } else
        {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Anomaly Detector upload FAILED");

        }
        alert.showAndWait();
    }

    @Override
    public void update(Observable o, Object arg) {

        updateJoystick();
        setAlARMS();
        if(updateGraphs == true)
        {
            fillGraphs(str);
        }

    }
    public void updateJoystick()
    {
        joystick.rudder.setValue(mainVM.rudder.getValue());
        joystick.throttle.setValue(mainVM.throttle.getValue());
        joystick.aileron.setValue(normalize(mainVM.aileron.getValue()));
        joystick.elevators.setValue(normalize(mainVM.elevator.getValue()));
        joystick.update();
    }
    // This method receives a double range [-1,1] and converts it to double [25,175]
    public double normalize(double input)
    {
        double output = input;
        output = (((output + 1)/2)*150) + 25;
        return output;
    }

    public void toggleStick(ActionEvent event)
    {
        if(joystick.isVisible())
            joystick.setVisible(false);
        else
            joystick.setVisible(true);
    }
    public void toggleGraphs(ActionEvent event)
    {
        if(cb_graphs.isVisible()) {
            cb_graphs.setVisible(false);
        }
        else {
            cb_graphs.setVisible(true);
        }
    }
    public void toggleProps(ActionEvent event)
    {
        if(cb_props.isVisible()) {
            cb_props.setVisible(false);
            cb_vals.setVisible(false);
        }
        else {
            cb_props.setVisible(true);
            cb_vals.setVisible(true);
        }
    }
    public void togglePlayBack(ActionEvent event)
    {
        if(playback_bar.isVisible())
            playback_bar.setVisible(false);
        else
            playback_bar.setVisible(true);
    }
    public void toggleIndi(ActionEvent event)
    {
        if(indicators.isVisible())
            indicators.setVisible(false);
        else
            indicators.setVisible(true);
    }


    public void selected(MouseEvent mouseEvent) {
        Label selected = (Label) mouseEvent.getSource();
        clearLabels();
        selected.setTextFill(Color.color(0,0,1));
        updateGraphs = true;
        str = selected.getId();
        if(mainVM.playable != null)
            fillGraphs(str);
    }

    private void clearLabels() {
        height.setTextFill(Color.color(0,0,0));
        pitch.setTextFill(Color.color(0,0,0));
        roll.setTextFill(Color.color(0,0,0));
        direction.setTextFill(Color.color(0,0,0));
        speed.setTextFill(Color.color(0,0,0));
        yaw.setTextFill(Color.color(0,0,0));;
    }

    public void play()
    {
        mainVM.play();
    }
    public void pause()
    {
        mainVM.pause();
    }
    public void increase_speed()
    {
        if(pb_speed_d.getValue()< 5) {
        pb_speed_d.setValue(pb_speed_d.getValue()+0.25);
        }
    }
    public void decrease_speed()
    {
        if(pb_speed_d.getValue()>0.25) {
            pb_speed_d.setValue(pb_speed_d.getValue() - 0.25);
        }
    }
    public void fillGraphs(String str)
    {
        fillTimeGraph(str);
        fillCorrolationGraph(str);
        if(mainVM.detector!=null)
            fillAnoGraph(str,graph3);
    }

    private void fillCorrolationGraph(String str) {
        XYChart.Series seriesTime = new XYChart.Series();
        mainVM.fillSeriesC(seriesTime,str);
        graph2.getData().clear();
        graph2.getData().add(seriesTime);

    }

    private void fillTimeGraph(String str) {

        XYChart.Series seriesTime = new XYChart.Series();
        mainVM.fillSeriesT(seriesTime,str);
        graph1.getData().clear();
        graph1.getData().add(seriesTime);
    }
    private void fillAnoGraph(String str,LineChart<Number,Number> graph)
    {
        mainVM.fillAnoSeries(graph,str);
    }
    public void learnN()
    {
        mainVM.learnN();
        indi3.setFill(Color.GREEN);
    }
    public void detectAno()
    {
        mainVM.detect();
        indi4.setFill(Color.GREEN);
    }
    private void setAlARMS()
    {
        if(mainVM.showAlarm)
        {
            cb_graphs.setStyle("-fx-background-color:#d71f1f;");

        }else
        {
            cb_graphs.setStyle("-fx-background-color:#f4f4f4;");
        }
    }
    




}

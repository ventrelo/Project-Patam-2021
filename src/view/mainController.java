package view;




import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.joystick.Joystick;
import vm.mainVM;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class mainController implements Observer {

    @FXML
    VBox cb_graphs,cb_props,cb_vals;
    @FXML
    AnchorPane playback_bar;
    @FXML
    Slider js_playback_bar;
    @FXML
    Joystick joystick;
    @FXML
    Label cb_height,cb_speed,cb_dir,cb_roll,cb_pitch,cb_yaw;
    @FXML
    Label pb_speed, js_vid_time;

    DoubleProperty pb_speed_d = new SimpleDoubleProperty(1.00);


    public vm.mainVM mainVM;
    public void setMainVM(mainVM vm)
    {
        this.mainVM = vm;
        mainVM.parseXML("./Assets/xmlSettings");
        // Clock Board labels binding
        cb_height.textProperty().bind(mainVM.height.asString());
        cb_speed.textProperty().bind(mainVM.speed.asString());
        cb_dir.textProperty().bind(mainVM.direction.asString());
        cb_roll.textProperty().bind(mainVM.roll.asString());
        cb_pitch.textProperty().bind(mainVM.pitch.asString());
        cb_yaw.textProperty().bind(mainVM.yaw.asString());
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
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML file (*.xml)","*.xml");
        fil_chooser.getExtensionFilters().add(extensionFilter);
        File file = fil_chooser.showOpenDialog(stage);
        Alert alert;
        if (file != null) {
            try {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get("./Assets/xmlSettings"),StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainVM.parseXML("./Assets/xmlSettings");
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("XML settings upload successful");
        } else
        {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("XML settings upload FAILED");
        }
        alert.showAndWait();
    }
    public void uploadCSVsett(ActionEvent event) {

        Stage stage = new Stage();
        FileChooser fil_chooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV file (*.csv)","*.csv");
        fil_chooser.getExtensionFilters().add(extensionFilter);
        File file = fil_chooser.showOpenDialog(stage);
        Alert alert;
        if (file != null) {
            mainVM.setPlayable(file.getAbsolutePath());
            js_playback_bar.maxProperty().setValue(mainVM.playable.MaxFrame);
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("CSV  upload successful");
        } else
        {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("CSV upload FAILED");
        }
        alert.showAndWait();
    }
    public void uploadDetectorPath(ActionEvent event)
    {
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("out"));
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
        if(cb_graphs.isVisible())
            cb_graphs.setVisible(false);
        else
            cb_graphs.setVisible(true);
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


    public void selected(MouseEvent mouseEvent) {
        Node selected = (Node) mouseEvent.getSource();
        String str = selected.getId();
        System.out.println(str);
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
}

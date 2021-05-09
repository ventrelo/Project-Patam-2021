package view;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import vm.mainVM;


import java.io.File;

import java.util.Observable;
import java.util.Observer;

public class mainController implements Observer {



    @FXML
    Canvas js_joystick;
    @FXML
    Label cb_height;
    @FXML
    Label cb_speed;
    @FXML
    Label cb_dir;
    @FXML
    Label cb_roll;
    @FXML
    Label cb_pitch;
    @FXML
    Label cb_yaw;

    JoyStick joyStick;
    public vm.mainVM mainVM;
    public void setMainVM(mainVM vm)
    {
        this.mainVM = vm;
        // Clock Board labels binding
        cb_height.textProperty().bind(mainVM.height.asString());
        cb_speed.textProperty().bind(mainVM.speed.asString());
        cb_dir.textProperty().bind(mainVM.direction.asString());
        cb_roll.textProperty().bind(mainVM.roll.asString());
        cb_pitch.textProperty().bind(mainVM.pitch.asString());
        cb_yaw.textProperty().bind(mainVM.yaw.asString());


    }
    public void init()
    {
        joyStick = new JoyStick();
        joyStick.init(js_joystick);
    }
    public void openFileExplorer(ActionEvent event) {

        Stage stage = new Stage();
        FileChooser fil_chooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML file (*.xml)","*.xml");
        fil_chooser.getExtensionFilters().add(extensionFilter);
        File file = fil_chooser.showOpenDialog(stage);
        Alert alert;
        if (file != null) {

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("XML settings upload successful");
        } else
        {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("XML settings upload FAILED");
        }
        alert.showAndWait();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

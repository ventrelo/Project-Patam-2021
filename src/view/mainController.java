package view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import vm.mainVM;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class mainController implements Observer {


    public vm.mainVM mainVM;
    public void setMainVM(mainVM vm)
    {
        this.mainVM = vm;

    }

    public void openFileExplorer(ActionEvent event) {

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FileChooser fil_chooser = new FileChooser();
        File file = fil_chooser.showOpenDialog(stage);

        if (file != null) {

            mainVM.parseXML(file.getAbsolutePath());
                if(mainVM.xmlSettings != null)
                {
                    System.out.println(mainVM.xmlSettings);
                }
                else System.out.println("Fail");
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

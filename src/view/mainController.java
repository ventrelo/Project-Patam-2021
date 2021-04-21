package view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class mainController {

     public vm.mainVM mainVM;
     private HashMap xmlSettings = null;








    public void openFileExplorer(ActionEvent event) {

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FileChooser fil_chooser = new FileChooser();
        File file = fil_chooser.showOpenDialog(stage);

        if (file != null) {

            xmlSettings = model.XMLParser.decodeXML(file.getAbsolutePath());
                if(xmlSettings != null)
                {
                    System.out.println(xmlSettings);
                }
                else System.out.println("Fail");
        }
    }
}

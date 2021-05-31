package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.XMLParserModel;
import vm.mainVM;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        mainController mainC; // Main Controller
        model.XMLParserModel xmlParserModel = new XMLParserModel(); //xmlParserModel
        vm.mainVM mainVM = new mainVM(xmlParserModel); //main View Model
        xmlParserModel.addObserver(mainVM);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainView.fxml"));
        Parent root = loader.load();
        mainC = loader.getController();
        mainC.setMainVM(mainVM);
        mainVM.addObserver(mainC);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 900, 550));
        primaryStage.show();
        mainC.init();







    }


    public static void main(String[] args) {
        launch(args);
    }
}

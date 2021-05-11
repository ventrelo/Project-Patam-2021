package view.joystick;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class Joystick extends BorderPane {
    public DoubleProperty aileron, elevators,rudder,throttle;
    public Joystick(){
        super();
        try {
            FXMLLoader fxl = new FXMLLoader();
            BorderPane joy = fxl.load(getClass().getResource("joystick.fxml").openStream());

            JoystickController joystickController = fxl.getController();
            this.getChildren().add(joy);
            joystickController.init();
            rudder = joystickController.js_rudder.valueProperty();
            throttle = joystickController.js_throttle.valueProperty();
            aileron = joystickController.jx;
            elevators = joystickController.jy;


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void update(DoubleProperty aileron,DoubleProperty elevators, DoubleProperty rudder, DoubleProperty throttle)
    {

    }


}

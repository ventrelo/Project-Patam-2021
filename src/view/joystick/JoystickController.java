package view.joystick;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;

import java.awt.*;

public class JoystickController {

    @FXML
    Canvas js_joystick;
    @FXML
    Slider js_throttle;
    @FXML
    Slider js_rudder;
    GraphicsContext gc;
    double size;
    DoubleProperty jx,jy;
    public void init()
    {

        jx = new SimpleDoubleProperty();
        jy = new SimpleDoubleProperty();
        gc = js_joystick.getGraphicsContext2D();
        jx.setValue(js_joystick.getWidth()/2);
        jy.setValue(js_joystick.getHeight()/2);
        size = 50;
        gc.fillOval(jx.getValue()-(size/2),jy.getValue()-(size/2),size,size);
    }
    public void execute()
    {

    }


}

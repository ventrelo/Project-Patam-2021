package view.joystick;

import javafx.beans.property.DoubleProperty;
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
    double size,jx,jy;
    public void init()
    {
        gc = js_joystick.getGraphicsContext2D();
        jx = js_joystick.getWidth()/2;
        jy = js_joystick.getHeight()/2;
        size = 50;
        gc.fillOval(jx-(size/2),jy-(size/2),size,size);
    }


}

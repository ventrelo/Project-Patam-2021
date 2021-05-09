package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class JoyStick implements F_Component{

    GraphicsContext gc;
    double jx,jy;
    double size;



    @Override
    public void init(Canvas canvas) {
        gc = canvas.getGraphicsContext2D();
        jx = canvas.getWidth()/2;
        jy = canvas.getHeight()/2;
        size = 50;
        gc.fillOval(jx-(size/2),jy-(size/2),size,size);

    }

    @Override
    public void execute() {

    }
}

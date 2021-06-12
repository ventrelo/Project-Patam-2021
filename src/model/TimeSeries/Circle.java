package model.TimeSeries;

public class Circle {
    Point center;
    float radius;

    public Circle() {
        this.center = new Point(0,0);
        this.radius = 0;
    }

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean encapsulates(Point point) {
        if(StatLib.distance(center,point) <= radius)
            return true;
        return false;
    }

    public boolean isValid() {
        if(center.x == 0 && center.y == 0 && radius == 0)
            return false;
        return true;
    }
}

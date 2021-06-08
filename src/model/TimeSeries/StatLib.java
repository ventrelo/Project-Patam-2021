package model.TimeSeries;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class StatLib {


	// simple average
	public static float avg(float[] x) {
		if (x.length == 0) {
			return 0;
		}

		float sum = 0;
		for (float num : x) sum += num;
		return sum / x.length;
	}

	// returns the variance of X and Y
	public static float var(float[] x) {
		if (x.length == 0) {
			return 0;
		}

		float u = avg(x);
		float[] xTemp = new float[x.length];
		for (int i = 0; i < xTemp.length; i++) {
			xTemp[i] = (float) Math.pow(x[i], 2);
		}
		return (float) (avg(xTemp) - Math.pow(u, 2));


	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y) {
		float[] xTemp = x.clone();
		for (int i = 0; i < xTemp.length; i++) {
			xTemp[i] = x[i] * y[i];
		}
		return avg(xTemp) - avg(x) * avg(y);

	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y) {

		return (float) ((cov(x, y)) / (Math.sqrt(var(x)) * Math.sqrt(var(y))));
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points) {
		float a;
		float b;
		float[] xPoints, yPoints;
		int size = points.length;
		xPoints = new float[size];
		yPoints = new float[size];
		for (int i = 0; i < size; i++) {
			xPoints[i] = points[i].x;
			yPoints[i] = points[i].y;
		}
		a = cov(xPoints, yPoints) / var(xPoints);
		b = avg(yPoints) - a * avg(xPoints);
		Line res = new Line(a, b);
		return res;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p, Point[] points) {

		Line res = linear_reg(points);
		return Math.abs(res.f(p.x) - p.y);
	}

	// returns the deviation between point p and the line
	public static float dev(Point p, Line l) {

		return Math.abs(l.f(p.x) - p.y);
	}

	public static Point[] makePointsArray(float[] x, float[] y) {
		Point[] retArr = new Point[x.length];
		for (int i = 0; i < x.length; i++) {
			retArr[i] = new Point(x[i], y[i]);
		}
		return retArr;
	}

	public static <T> ArrayList<T> fromArrayToArrayList(T[] arr) {
		return (ArrayList<T>) Arrays.stream(arr).collect(Collectors.toList());
	}

	public static Circle findCircle(Point p1, Point p2, Point p3) {
		float x12 = p1.x - p2.x;
		float x13 = p1.x - p3.x;

		float y12 = p1.y - p2.y;
		float y13 = p1.y - p3.y;

		float y31 = p3.y - p1.y;
		float y21 = p2.y - p1.y;

		float x31 = p3.x - p1.x;
		float x21 = p2.x - p1.x;

		// x1^2 - x3^2
		float sx13 = (float) (Math.pow(p1.x, 2) - Math.pow(p3.x, 2));

		// y1^2 - y3^2
		float sy13 = (float) (Math.pow(p1.y, 2) - Math.pow(p3.y, 2));
		float sx21 = (float) (Math.pow(p2.x, 2) - Math.pow(p1.x, 2));
		float sy21 = (float) (Math.pow(p2.y, 2) - Math.pow(p1.y, 2));

		float f = ((sx13) * (x12)
				+ (sy13) * (x12)
				+ (sx21) * (x13)
				+ (sy21) * (x13))
				/ (2 * ((y31) * (x12) - (y21) * (x13)));
		float g = ((sx13) * (y12)
				+ (sy13) * (y12)
				+ (sx21) * (y13)
				+ (sy21) * (y13))
				/ (2 * ((x31) * (y12) - (x21) * (y13)));

		float c = -(float) Math.pow(p1.x, 2) - (float) Math.pow(p1.y, 2) - 2 * g * p1.x - 2 * f * p1.y;

		// eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
		// where centre is (h = -g, k = -f) and radius r
		// as r^2 = h^2 + k^2 - c
		float h = -g;
		float k = -f;
		float sqr_of_r = h * h + k * k - c;

		// r is the radius
		float r = (float) Math.sqrt(sqr_of_r);

		return new Circle(new Point(h,k), r);
	}

	public static float distance(Point p1, Point p2) {
		return (float) Math.sqrt(Math.pow(p1.x - p2.x,2) + Math.pow(p1.y-p2.y,2));
	}

	public static Circle welzl(ArrayList<Point> points, ArrayList<Point> R) {
		if (points.isEmpty() || R.size() == 3) {
			if (R.size() == 3)
				return findCircle(R.get(0), R.get(1), R.get(2));
			else if(R.size() == 2) {
				Point p1 = R.get(0);
				Point p2 = R.get(1);
				Point centerPoint = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
				return new Circle(centerPoint, 0);
			} else if (R.size() == 1) {
				return new Circle(R.get(0), 0);
			} else {
				return new Circle();
			}
		}

		Random rnd = new Random();
		int random_index = rnd.nextInt(points.size());
		Point p = points.remove(random_index);
		Circle D = welzl(points, R);

		if(D.encapsulates(p)) {
			return D;
		}

		R.add(p);
		return welzl(points, R);
	}

	public static float getMaxPointDistance(Point[] points, Point center) {
		float maxDistance = 0;
		for(Point p: points) {
			float distance = distance(p, center);
			if (distance > maxDistance) {
				maxDistance = distance;
			}
		}
		return maxDistance;
	}
}
package model.TimeSeries;
import java.lang.Math;

public class StatLib {

	

	// simple average
	public static float avg(float[] x){
		float sum=0;
		for(float num : x) sum += num;
		return sum/x.length;
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float u = avg(x);
		float[] xTemp = new float[x.length];
		for (int i=0;i<xTemp.length;i++)
		{
			xTemp[i]=(float)Math.pow(x[i],2);
		}
		return (float) (avg(xTemp) - Math.pow(u,2));


	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float[] xTemp = x.clone();
		for(int i=0;i<xTemp.length;i++)
		{
			xTemp[i] = x[i] * y[i];
		}
		return avg(xTemp) - avg(x)*avg(y);

	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){

		return (float) ((cov(x,y))/(Math.sqrt(var(x)) * Math.sqrt(var(y))));
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float a;
		float b;
		float[] xPoints,yPoints;
		int size= points.length;
		xPoints = new float[size];
		yPoints = new float[size];
		for(int i=0;i<size;i++)
		{
			xPoints[i] = points[i].x;
			yPoints[i] = points[i].y;
		}
		a = cov(xPoints,yPoints) / var(xPoints);
		b = avg(yPoints) - a*avg(xPoints);
		Line res = new Line(a, b);
		return res;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){

		Line res = linear_reg(points);
		return Math.abs(res.f(p.x) - p.y);
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){

		return Math.abs(l.f(p.x) - p.y);
	}

	
}

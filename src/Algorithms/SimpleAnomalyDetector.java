package Algorithms;

import model.TimeSeries.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	List<CorrelatedFeatures> corrFeat;
	float threshold = 0.9f;

	public SimpleAnomalyDetector(){}

	public SimpleAnomalyDetector(float threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	@Override
	public void learnNormal(TimeSeries ts) {
		corrFeat = new ArrayList<>();

		List<String> header=ts.getHeaders();
		List<Float[]> values=ts.getValues();
		float maxCorr;
		float current;
		int maxIndex = 0;
		for(int i=0;i<header.size();i++)
		{
			maxCorr = 0;
			for(int j=i+1;j<header.size();j++)
			{
				current = Math.abs(StatLib.pearson(makeArray(values,i),makeArray(values,j)));
				if(current > maxCorr) {
					maxCorr = current;
					maxIndex = j;
				}
			}

			if(maxCorr > threshold) // Features will be definitely correlated if max is higher than 0.9,this is changeable
			{
				String f1 = header.get(i);
				String f2 = header.get(maxIndex);
				Point[] points;
				points = makePointsArray(makeArray(values,i),makeArray(values,maxIndex));
				Line line = StatLib.linear_reg(points);
				float maxDev = 0;
				for(Point p:points)
				{
					float temp = StatLib.dev(p,line);
					if(maxDev < temp)
					{
						maxDev = temp;
					}
				}
				maxDev*=1.1;
				this.corrFeat.add(new CorrelatedFeatures(f1,f2,maxCorr,line,maxDev));
			}
		}

	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<String> header=ts.getHeaders();
		List<Float[]> values=ts.getValues();
		List<AnomalyReport> retList = new ArrayList<>();
		Point[] points;
		int timeStep;
		int featureIndex1,featureIndex2;
		for (CorrelatedFeatures c:this.getNormalModel())
		{
			timeStep = 0;
			featureIndex1 = findFeatureIndex(header,1,c);
			featureIndex2 = findFeatureIndex(header,2,c);
			points = makePointsArray(makeArray(values,featureIndex1),makeArray(values,featureIndex2));
			for(Point p:points)
			{
				timeStep++;
				if(StatLib.dev(p,c.lin_reg) > c.threshold)
				{
					String f1 = header.get(featureIndex1);
					String f2 = header.get(featureIndex2);
					retList.add(new AnomalyReport(f1+"-"+f2, timeStep));
				}
			}

		}
		return retList;
	}
	
	public List<CorrelatedFeatures> getNormalModel(){
		return this.corrFeat;
	}

	public float[] makeArray(List<Float[]> val, int index){
		float[] retArr = new float[val.size()];
		for(int i=0;i<retArr.length;i++)
			retArr[i] = val.get(i)[index];
		return retArr;

	}
	public Point[] makePointsArray(float[] x,float[] y){
		Point[] retArr = new Point[x.length];
		for(int i=0;i<x.length;i++)
		{
			retArr[i] = new Point(x[i],y[i]);
		}
		return retArr;
	}
	public int findFeatureIndex(List<String> headers,int mode,CorrelatedFeatures c)
	{
		if(mode == 1)
		{
			for(int i=0;i< headers.size();i++)
			{
				String temp = headers.get(i);
				if(temp.equals(c.feature1))
					return i;
			}
		} else if(mode == 2)
		{
			for(int i=0;i< headers.size();i++)
			{
				String temp = headers.get(i);
				if(temp.equals(c.feature2))
					return i;
			}
		}
		return -1;
	}

	public HashMap learnNormalSingleton(float[] f1, float[] f2) {
		HashMap tmpHashMap = new HashMap();
		Point[] points;
		points = makePointsArray(f1,f2);
		Line line = StatLib.linear_reg(points);
		float maxDev = 0;
		for(Point p:points)
		{
			float temp = StatLib.dev(p,line);
			if(maxDev < temp)
			{
				maxDev = temp;
			}
		}
		maxDev*=1.1;

		tmpHashMap.put("line", line);
		tmpHashMap.put("maxDev", maxDev);
		return tmpHashMap;
	}
}

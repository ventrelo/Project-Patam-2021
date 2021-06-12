package Algorithms;

import model.TimeSeries.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hybrid implements TimeSeriesAnomalyDetector {
    ArrayList<HashMap<String, Object>> correlationsData;

    public Hybrid() {
        correlationsData = new ArrayList<>();
    }

    public ArrayList<CorrelatedFeatures> getCorrelations(TimeSeries ts) {
        ArrayList<CorrelatedFeatures> correlations = new ArrayList<>();
        List<String> headers = ts.getHeaders();

        for(int i = 0; i < headers.size(); i++) {
            float[] current_header_values = ts.getValuesOfHeader(headers.get(i));
            int most_correlated_header_index = -1;
            float max_correlation = 0;

            for(int j = i + 1; j < headers.size(); j++) {
                float[] compared_header_values = ts.getValuesOfHeader(headers.get(j));
                float correlation = Math.abs(StatLib.pearson(current_header_values, compared_header_values));

                if(correlation > max_correlation) {
                    max_correlation = correlation;
                    most_correlated_header_index = j;
                }
            }

            if(most_correlated_header_index != -1) {
                Point[] points = StatLib.makePointsArray(ts.getValuesOfHeader(headers.get(i)),ts.getValuesOfHeader(headers.get(most_correlated_header_index)));
                Line line = StatLib.linear_reg(points);
                correlations.add(new CorrelatedFeatures(
                        headers.get(i),
                        headers.get(most_correlated_header_index),
                        max_correlation,
                        line,
                        0.9f
                ));
            }
        }

        return correlations;
    }

    public void learnNormal(TimeSeries ts) {
        ArrayList<CorrelatedFeatures> correlatedFeatures = getCorrelations(ts);
        SimpleAnomalyDetector sad = new SimpleAnomalyDetector();

        correlatedFeatures.forEach(correlatedFeature -> {
            HashMap<String,Object> tmpHashMap = new HashMap<>();
            float[] f1 = ts.getValuesOfHeader(correlatedFeature.feature1);
            float[] f2 = ts.getValuesOfHeader(correlatedFeature.feature2);

            if(correlatedFeature.corrlation >= 0.95) {
                HashMap returnedHashMap = sad.learnNormalSingleton(f1,f2);
                tmpHashMap.put("algorithm", "linear");
                tmpHashMap.put("feature1", correlatedFeature.feature1);
                tmpHashMap.put("feature2", correlatedFeature.feature2);
                tmpHashMap.put("line", returnedHashMap.get("line"));
                tmpHashMap.put("maxDev", returnedHashMap.get("maxDev"));
            } else if(correlatedFeature.corrlation >= 0.5) {
                Point[] points = StatLib.makePointsArray(f1,f2);
                ArrayList<Point> pointsArrayList = StatLib.fromArrayToArrayList(points);
                Circle circle = StatLib.welzl(pointsArrayList, new ArrayList<>());
                float maxDistance = circle.getRadius();

                tmpHashMap.put("algorithm", "hybrid");
                tmpHashMap.put("feature1", correlatedFeature.feature1);
                tmpHashMap.put("feature2", correlatedFeature.feature2);
                tmpHashMap.put("circle", circle);
                tmpHashMap.put("maxDistance", maxDistance);
            } else {
                float[] headerValues = ts.getValuesOfHeader(correlatedFeature.feature1);
                tmpHashMap.put("algorithm", "zscore");
                tmpHashMap.put("feature1", correlatedFeature.feature1);
                tmpHashMap.put("maxZScore",ZScore.learnNormalSingleton(headerValues));
            }

            correlationsData.add(tmpHashMap);
        });
    }

    public List<AnomalyReport> detect(TimeSeries ts) {
        ArrayList anomalies = new ArrayList();

        correlationsData.forEach(correlationData -> {
            HashMap<String, Object> tmpHashMap = new HashMap<String, Object>();

            if(correlationData.get("algorithm").equals("linear")) {
                String feature1 = (String) correlationData.get("feature1");
                String feature2 = (String) correlationData.get("feature2");
                float maxDev = (float) correlationData.get("maxDev");
                Line line = (Line) correlationData.get("line");
                Point[] points = StatLib.makePointsArray(ts.getValuesOfHeader(feature1),ts.getValuesOfHeader(feature2));

                for (int i = 0; i < points.length; i++)
                    if (Math.abs(line.a * points[i].x + line.b - points[i].y) > maxDev)
                        anomalies.add(new AnomalyReport(feature1 + "-" + feature2, i));
            } else if(correlationData.get("algorithm").equals("hybrid")) {
                String feature1 = (String) correlationData.get("feature1");
                String feature2 = (String) correlationData.get("feature2");
                Circle circle = (Circle) correlationData.get("circle");
                float maxDistance = (float) correlationData.get("maxDistance");

                Point[] points = StatLib.makePointsArray(ts.getValuesOfHeader(feature1),ts.getValuesOfHeader(feature2));

                for (int i = 0; i < points.length; i++)
                    if (StatLib.distance(points[i], circle.getCenter()) > maxDistance)
                        anomalies.add(new AnomalyReport(feature1 + "-" + feature2, i));
            } else {
                String feature1 = (String) correlationData.get("feature1");
                float[] headerValues = ts.getValuesOfHeader(feature1);
                float maxZScore = (float) correlationData.get("maxZScore");

                for (int i = 0; i < headerValues.length; i++)
                    if(ZScore.detectSingleton(headerValues,i) > maxZScore)
                        anomalies.add(new AnomalyReport(feature1, i));
            }
        });

        return anomalies;
    }



    public ArrayList<HashMap<String, Object>> getCorrelationsData() {
        return correlationsData;
    }
}
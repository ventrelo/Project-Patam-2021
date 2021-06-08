package Algorithms;

import model.TimeSeries.AnomalyReport;
import model.TimeSeries.StatLib;
import model.TimeSeries.TimeSeries;
import model.TimeSeries.TimeSeriesAnomalyDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZScore implements TimeSeriesAnomalyDetector {
    private final ArrayList<Float> maxZScores;

    public ZScore() {
        maxZScores = new ArrayList<>();
    }

    public void learnNormal(TimeSeries ts) {
        // initialize the maximum values at start
        for(int i = 0; i < ts.getHeaders().size(); i++) {
            maxZScores.add(0f);
        }

        int headerCounter = 0;

        for(String header: ts.getHeaders()) {
            float[] headerValues = ts.getValuesOfHeader(header);

            for(int i = 0; i < headerValues.length; i++) {
                float[] currentlyUsedValues = new float[i];
                float currentZScore = 0;

                System.arraycopy(headerValues, 0, currentlyUsedValues, 0, i);
                float currentX = headerValues[i];
                float average = StatLib.avg(currentlyUsedValues);
                float standardDeviation = (float) Math.sqrt(StatLib.var(currentlyUsedValues));

                if (standardDeviation != 0) {
                    currentZScore = Math.abs(currentX - average) / standardDeviation;
                }

                if(currentZScore > maxZScores.get(headerCounter)) {
                    maxZScores.set(headerCounter, currentZScore);
                }
            }

            headerCounter++;
        }
    }

    public List<AnomalyReport> detect(TimeSeries ts) {
        List<AnomalyReport> anomalies = new ArrayList<>();
        int headerCounter = 0;

        for(String header: ts.getHeaders()) {
            float[] headerValues = ts.getValuesOfHeader(header);

            for(int i = 0; i < headerValues.length; i++) {
                float[] currentlyUsedValues = new float[i];
                float currentZScore = 0;

                System.arraycopy(headerValues, 0, currentlyUsedValues, 0, i);
                float currentX = headerValues[i];
                float average = StatLib.avg(currentlyUsedValues);
                float standardDeviation = (float) Math.sqrt(StatLib.var(currentlyUsedValues));

                if (standardDeviation != 0) {
                    currentZScore = Math.abs(currentX - average) / standardDeviation;
                }

                if(currentZScore > maxZScores.get(headerCounter)) {
                    anomalies.add(new AnomalyReport(header, i));
                }
            }

            headerCounter++;
        }

        return anomalies;
    }

    public static float learnNormalSingleton(float[] headerValues) {
        float maxZscore = 0;

        for(int i = 0; i < headerValues.length; i++) {
            float[] currentlyUsedValues = new float[i];
            float currentZScore = 0;

            System.arraycopy(headerValues, 0, currentlyUsedValues, 0, i);
            float currentX = headerValues[i];
            float average = StatLib.avg(currentlyUsedValues);
            float standardDeviation = (float) Math.sqrt(StatLib.var(currentlyUsedValues));

            if (standardDeviation != 0) {
                currentZScore = Math.abs(currentX - average) / standardDeviation;
            }

            if(currentZScore > maxZscore) {
                maxZscore = currentZScore;
            }
        }

        return maxZscore;
    }

    public static float detectSingleton(float[] headerValues, int i) {
        float currentZScore = 0;
        float[] currentlyUsedValues = new float[i];

        System.arraycopy(headerValues, 0, currentlyUsedValues, 0, i);
        float currentX = headerValues[i];
        float average = StatLib.avg(currentlyUsedValues);
        float standardDeviation = (float) Math.sqrt(StatLib.var(currentlyUsedValues));

        if (standardDeviation != 0) {
            currentZScore = Math.abs(currentX - average) / standardDeviation;
        }

        return currentZScore;
    }
}
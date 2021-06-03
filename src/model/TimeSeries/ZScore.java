package model.TimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZScore implements TimeSeriesAnomalyDetector{
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
            float standardDeviation = (float) Math.sqrt(StatLib.var(headerValues));

            for(int i = 0; i < headerValues.length; i++) {
                float[] currentlyUsedValues = new float[i];
                float currentZScore = 0;

                System.arraycopy(headerValues, 0, currentlyUsedValues, 0, i);
                float currentX = headerValues[i];
                float average = StatLib.avg(currentlyUsedValues);
                currentZScore = Math.abs(currentX - average) / standardDeviation;

                if(currentZScore > maxZScores.get(headerCounter)) {
                    maxZScores.set(headerCounter, currentZScore);
                }
            }

            headerCounter++;
        }

        System.out.println("learning ended");
    }

    public List<AnomalyReport> detect(TimeSeries ts) {
        List<AnomalyReport> anomalies = new ArrayList<>();
        int headerCounter = 0;

        for(String header: ts.getHeaders()) {
            float[] headerValues = ts.getValuesOfHeader(header);
            float standardDeviation = (float) Math.sqrt(StatLib.var(headerValues));

            for(int i = 0; i < headerValues.length; i++) {
                float[] currentlyUsedValues = new float[i];
                float currentZScore = 0;

                System.arraycopy(headerValues, 0, currentlyUsedValues, 0, i);
                float currentX = headerValues[i];
                float average = StatLib.avg(currentlyUsedValues);
                currentZScore = Math.abs(currentX - average) / standardDeviation;

                if(currentZScore > maxZScores.get(headerCounter)) {
                    anomalies.add(new AnomalyReport(header, i));
                }
            }

            headerCounter++;
        }

        System.out.println("detection ended");
        return anomalies;
    }
}

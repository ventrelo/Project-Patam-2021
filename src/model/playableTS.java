package model;

import model.TimeSeries.TimeSeries;

import java.util.Observable;

public class playableTS extends Observable implements playable  {
    public int MaxFrame;
    TimeSeries timeSeries;
    double [] values;
    @Override
    public void play(int frame) {

    }

    public void setTimeSeries(String path) {
        this.timeSeries = new TimeSeries(path);
    }

    @Override
    public int pause() {
        return 0;
    }
}

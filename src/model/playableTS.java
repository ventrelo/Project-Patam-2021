package model;

import model.TimeSeries.TimeSeries;

import java.util.Observable;

public class playableTS extends Observable implements playable  {
    public int MaxFrame;
    public TimeSeries timeSeries;
    public Float[] values;
    @Override
    public void play(int frame) {
        values = timeSeries.getValues().get(frame);
        setChanged();
        notifyObservers();
    }

    public void setTimeSeries(String path) {
        this.timeSeries = new TimeSeries(path);
        MaxFrame = timeSeries.getValues().size()-1;
    }

    @Override
    public int pause() {
        return 0;
    }
}

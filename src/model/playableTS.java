package model;

import model.TimeSeries.TimeSeries;

public class playableTS implements playable{
    int frame;
    TimeSeries timeSeries;
    @Override
    public void play() {

    }

    @Override
    public int pause() {
        return 0;
    }
}

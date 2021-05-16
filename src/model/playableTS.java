package model;

import model.TimeSeries.TimeSeries;

public class playableTS implements playable{
    int frame;
    TimeSeries timeSeries;
    @Override
    public void play(int startFrame) {

    }

    @Override
    public int pause() {
        return 0;
    }
}

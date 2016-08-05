package com.bp_android.prium.beeping_android.temp;

/**
 * Created by vaibhavbhalerao on 3/17/16.
 */
public class SegmentPosition {

    public int order;
    public double start_lat;
    public double start_lng;
    public double end_lat;
    public double end_lng;


    public SegmentPosition(int order, double start_lat, double start_lng, double end_lat, double end_lng) {
        this.order = order;
        this.start_lat = start_lat;
        this.start_lng = start_lng;
        this.end_lat = end_lat;
        this.end_lng = end_lng;
    }

}

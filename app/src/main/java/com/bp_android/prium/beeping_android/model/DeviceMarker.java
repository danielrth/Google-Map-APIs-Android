package com.bp_android.prium.beeping_android.model;

/**
 * Created by vaibhavbhalerao on 3/11/16.
 */
public class DeviceMarker {

    private String title;
    private double latitude;
    private double longitude;
    private int picture;
    private String currentAddress;

    public DeviceMarker(String title, double latitude, double longitude, int picture, String currentAddress) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.picture = picture;
        this.currentAddress = currentAddress;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getPicture() {
        return picture;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

}

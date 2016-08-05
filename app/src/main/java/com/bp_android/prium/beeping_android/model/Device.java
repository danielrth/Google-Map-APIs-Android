package com.bp_android.prium.beeping_android.model;

import java.io.Serializable;

/**
 * Created by Vaibhav on 3/1/16.
 */
public class Device implements Serializable {

    private String serialNumber;
    private int id;
    private String name;
    private String bluetoothUuid;
    private String currentAddress;
    private int picture;

    public Device(String serialNumber, int id, String name, String bluetoothUuid, String currentAddress, int picture) {
        this.serialNumber = serialNumber;
        this.id = id;
        this.name = name;
        this.bluetoothUuid = bluetoothUuid;
        this.currentAddress = currentAddress;
        this.picture = picture;
    }

    public Device(String name, int picture) {
        this.name = name;
        this.picture = picture;
    }

    public Device(int id, String serialNumber) {
        this.id = id;
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBluetoothUuid() {
        return bluetoothUuid;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public int getPicture() {
        return picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBluetoothUuid(String bluetoothUuid) {
        this.bluetoothUuid = bluetoothUuid;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    @Override
    public boolean equals(Object o) {
        return ((Device) o).getId() == id;
    }

}

package com.bikesharedemo.login;

import java.io.Serializable;

public class BikeStatus implements Serializable {

    private String bikenumber;
    private Integer bikebattery;
    private String bikestation;
    private String bikeaddress;

    public BikeStatus() {
    }

    public BikeStatus(String bikenumber, Integer bikebattery, String bikestation, String bikeaddress) {
        this.bikenumber = bikenumber;
        this.bikebattery = bikebattery;
        this.bikestation = bikestation;
        this.bikeaddress = bikeaddress;
    }

    public String getBikenumber() { return bikenumber; }

    public void setBikenumber(String bikenumber) {
        this.bikenumber = bikenumber;
    }

    public Integer getBikebattery() { return bikebattery; }

    public void setBikebattery(Integer bikebattery) {
        this.bikebattery = bikebattery;
    }

    public String getBikestation() { return bikestation; }

    public void setBikestation(String bikestation) {
        this.bikestation = bikestation;
    }

    public String getBikeaddress() { return bikeaddress; }

    public void setBikeaddress(String bikeaddress) {
        this.bikeaddress = bikeaddress;
    }


}

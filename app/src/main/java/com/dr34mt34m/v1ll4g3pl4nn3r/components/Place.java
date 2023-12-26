package com.dr34mt34m.v1ll4g3pl4nn3r.components;

import com.dr34mt34m.v1ll4g3pl4nn3r.waitTimes.WaitTimeCalculator;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Place {
    private String id;
    private String name;
    private List<String> curPeople;
    private LatLng location;
    private double waitMultiplier;

    public Place(String id, String name, LatLng location, List<String> curPeople, double waitMultiplier) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.curPeople = curPeople;
        this.waitMultiplier = waitMultiplier;
    }

    public Place(String name, List<String> curPeople, LatLng location, double waitMultiplier) {
        this.name = name;
        this.curPeople = curPeople;
        this.location = location;
        this.waitMultiplier = waitMultiplier;
    }

    public String toString() {
        return "{id=" + id + ", name=" + name + ", location=" + location + ", curPeople=" + curPeople + ", waitMultiplier=" + waitMultiplier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumPeople() {
        return curPeople.size();
    }

    public List<String> getCurPeople() {
        return curPeople;
    }

    public double getWaitTime() {
        return WaitTimeCalculator.getWaitTimeSeconds(this);
    }

    public LatLng getLocation() {
        return location;
    }

    public double getWaitMultiplier() {
        return waitMultiplier;
    }

    public void setWaitMultiplier(double waitMultiplier) {
        this.waitMultiplier = waitMultiplier;
    }

}

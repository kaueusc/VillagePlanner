package com.dr34mt34m.v1ll4g3pl4nn3r.waitTimes;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Place;

public class WaitTimeCalculator {
    public static double getWaitTimeSeconds(Place place) {
        // get the number of people from this place
        int numberOfPeople = place.getNumPeople();
        // calculate the result based on the wait time multiplier
        double waitTimeMultiplier = place.getWaitMultiplier();

        return numberOfPeople * waitTimeMultiplier;
    }
}

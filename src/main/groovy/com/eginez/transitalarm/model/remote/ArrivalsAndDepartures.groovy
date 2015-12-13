package com.eginez.transitalarm.model.remote

import groovy.transform.ToString
import groovy.transform.builder.Builder
import org.joda.time.DateTime

import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Builder
@ToString(includeNames = true)
class ArrivalsAndDepartures {
    String routeId
    String routeLongName
    String routeShortName
    long scheduledArrivalTime
    long scheduledDepartureTime
    String frequency
    boolean predicted
    long predictedArrivalTime
    long predictedDepartureTime
    double distanceFromStop
    int numberOfStopsAway
    boolean arrivalEnabled
    boolean departureEnabled
    int stopSequence

    def propertyMissing(String property, def value) {
        //no-op
    }

    def String prettyPrint() {
        return "Name: $routeShortName, " +
                //"scheduleDepartureTime: ${formatDate(scheduledDepartureTime)},  " +
                "predictedDepartureTime: ${formatDate(predictedDepartureTime)} " +
                "numberOfStopsAway: ${numberOfStopsAway}"
    }

    def String formatDate(long date) {
        return new DateTime(date).toString('HH:mm')
    }

}

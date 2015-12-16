package com.eginez.transitalarm.model.remote

import groovy.transform.ToString
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime


@ToString(includeNames = true)
class ArrivalsAndDepartures {
    boolean arrivalEnabled
    String blockTripSequence
    boolean departureEnabled
    double distanceFromStop
    String frequency
    LocalDateTime lastUpdateTime
    int numberOfStopsAway
    boolean predicted
    String predictedArrivalInterval
    LocalDateTime predictedArrivalTime
    String predictedDepartureInterval
    LocalDateTime predictedDepartureTime
    String routeId
    String routeLongName
    String routeShortName
    String scheduledArrivalInterval
    LocalDateTime scheduledArrivalTime
    String scheduledDepartureInterval
    LocalDateTime scheduledDepartureTime
    LocalDateTime serviceDate
    List<String> situationIds
    String status
    String stopId
    int stopSequence
    String tripHeadsign
    String tripId
    TripStatus tripStatus
    String vehicleId

    def propertyMissing(String property, def value) {
        //no-op
    }

    def String prettyPrint() {
        return "Name: $routeShortName, " +
                //"scheduleDepartureTime: ${formatDate(scheduledDepartureTime)},  " +
                "predictedDepartureTime: ${formatDate(predictedDepartureTime)} " +
                "numberOfStopsAway: ${numberOfStopsAway}"
    }

    def String formatDate(LocalDateTime date) {
        return date.toString('HH:mm')
    }

}

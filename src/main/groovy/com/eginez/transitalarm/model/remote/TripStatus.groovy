package com.eginez.transitalarm.model.remote

import org.joda.time.LocalDateTime

class TripStatus {
    String activeTripId
    int blockTripSequence
    String closestStop
    int closestStopTimeOffset
    double distanceAlongTrip
    String frequency
    int lastKnownDistanceAlongTrip
    Map<String, Double> lastKnownLocation
    int lastKnownOrientation
    LocalDateTime lastLocationUpdateTime
    LocalDateTime lastUpdateTime
    String nextStop
    int nextStopTimeOffset
    double orientation
    Map<String, Double> position
    boolean predicted
    int scheduleDeviation
    double scheduledDistanceAlongTrip
    LocalDateTime serviceDate
    List<String> situationIds
    double totalDistanceAlongTrip
    String vehicleId
}

package com.eginez.transitalarm.services

import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Stop
import org.joda.time.LocalDateTime
import rx.observables.ConnectableObservable

interface TransitMonitor {
    public ConnectableObservable<List<Tuple2<Stop, ArrivalsAndDepartures>>> startMonitorBusAtStop(LocalDateTime startAt, String routeName, String stopCode, String tripName, int duration)
    public ConnectableObservable<List<Tuple2<Stop, ArrivalsAndDepartures>>> updateMonitorBusAtStop(LocalDateTime startAt, String routeName, String stopCode, String tripName, int duration)
}
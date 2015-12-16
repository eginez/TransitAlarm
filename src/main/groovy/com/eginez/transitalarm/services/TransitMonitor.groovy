package com.eginez.transitalarm.services

import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Stop
import org.joda.time.DateTime
import rx.observables.ConnectableObservable

interface TransitMonitor {
    public ConnectableObservable<List<Tuple2<Stop, ArrivalsAndDepartures>>> startMonitorBusAtStop(DateTime startAt, String routeName, String stopCode, String tripName)
}
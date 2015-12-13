package com.eginez.transitalarm.services

import org.joda.time.DateTime
import rx.observables.ConnectableObservable

interface TransitMonitor {
    public ConnectableObservable<String> startMonitorBusAtStop(DateTime startAt, String routeName, String stopCode, String tripName)
}
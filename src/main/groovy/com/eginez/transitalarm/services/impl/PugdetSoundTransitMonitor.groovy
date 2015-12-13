package com.eginez.transitalarm.services.impl

import com.eginez.transitalarm.services.TransitMonitor
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.Seconds
import rx.Observable
import rx.Subscriber
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers

import javax.inject.Inject
import java.util.concurrent.TimeUnit

import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class PugdetSoundTransitMonitor implements TransitMonitor {
    @Inject BusFinder busFinder
    int frequency = 5
    TimeUnit frequencyTimeUnit = SECONDS

    public PugdetSoundTransitMonitor(){ }

    public String displayBusInfo(String routeName, String stopCode, String tripName) {
        return busFinder.findBusInfoAlongRoute(routeName, stopCode, tripName)
    }

    public ConnectableObservable<String> startMonitorBusAtStop(DateTime startAt, String routeName, String stopCode, String tripName) {
        long initalDelay = calculateIntialDelay(startAt)
        log.debug("Will start in $initalDelay seconds")
        def obs = Observable.<String>create({ Subscriber subscriber ->
            Schedulers.newThread()
                    .createWorker()
                    .schedulePeriodically({ subscriber.onNext(displayBusInfo(routeName, stopCode, tripName)) },
                    initalDelay, frequency, frequencyTimeUnit)
        }).publish()
        return obs
    }

    long calculateIntialDelay(DateTime startAt) {
        Seconds delay = Seconds.secondsBetween(DateTime.now(), startAt)
        long seconds = Long.max(0, delay.seconds)
        return seconds
    }
}

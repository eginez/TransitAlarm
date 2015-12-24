package com.eginez.transitalarm.services.impl

import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Stop
import com.eginez.transitalarm.services.TransitMonitor
import groovy.transform.CompileStatic
import groovy.transform.ThreadInterrupt
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime
import org.joda.time.Seconds
import rx.Observable
import rx.Scheduler
import rx.Subscriber
import rx.Subscription
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers

import javax.inject.Inject
import java.util.concurrent.TimeUnit

import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
@CompileStatic
class PugdetSoundTransitMonitor implements TransitMonitor {
    @Inject BusFinder busFinder
    int frequency = 5
    TimeUnit frequencyTimeUnit = SECONDS
    Map<String, Subscription> allSubscriptions = [:]
    Map<String, Scheduler.Worker> allKillers = [:]

    public PugdetSoundTransitMonitor(){ }

    public List<Tuple2<Stop, ArrivalsAndDepartures>> displayBusInfo(String routeName, String stopCode, String tripName) {
        return busFinder.findBusInfoAlongRoute(routeName, stopCode, tripName)
    }

    public ConnectableObservable<List<Tuple2<Stop, ArrivalsAndDepartures>>> startMonitorBusAtStop(LocalDateTime startAt, String routeName,
                                                                                              String stopCode, String tripName, int duration) {
        long initialDelay = calculateIntialDelay(startAt)
        log.debug("Will start in $initialDelay seconds")
        def obs = Observable.<List<Tuple2<Stop, ArrivalsAndDepartures>>>create({ Subscriber subscriber ->
            Scheduler.Worker worker = Schedulers.newThread() .createWorker()
            Subscription subscription = worker.schedulePeriodically({
                try {
                    subscriber.onNext(displayBusInfo(routeName, stopCode, tripName))
                }catch(InterruptedIOException|InterruptedException ex) {
                    log.debug("good bye from ${Thread.currentThread().name}")
                }
            }, initialDelay, frequency, frequencyTimeUnit)
            allSubscriptions[routeName+stopCode+tripName] = subscription
            Scheduler.Worker killerWorker = Schedulers.newThread().createWorker()
            killerWorker.schedule({ log.info('Scheduled dead'); killMonitor(routeName, stopCode, tripName) }, duration, MINUTES)
            allKillers[routeName+stopCode+tripName] = killerWorker
        } as Observable.OnSubscribe).publish()
        return obs
    }

    @Override
    ConnectableObservable<List<Tuple2<Stop, ArrivalsAndDepartures>>> updateMonitorBusAtStop(LocalDateTime startAt, String routeName, String stopCode, String tripName, int duration) {
        killMonitor(routeName, stopCode, tripName)
        return startMonitorBusAtStop(startAt, routeName, stopCode, tripName, duration)
    }

    private void killMonitor(String routeName, String stopCode, String tripName){
        Subscription subscription = allSubscriptions.get(routeName+stopCode+tripName)
        if(subscription == null) {
            return
        }
        log.debug("Killing previous monitor for ${routeName} ${stopCode} ${tripName}")
        subscription.unsubscribe()
        allSubscriptions.remove(routeName+stopCode+tripName)
        allKillers.get(routeName+stopCode+tripName).unsubscribe()
        allKillers.remove(routeName+stopCode+tripName)
    }

    long calculateIntialDelay(LocalDateTime startAt) {
        Seconds delay = Seconds.secondsBetween(LocalDateTime.now(), startAt)
        long seconds = Long.max(0, delay.seconds)
        return seconds
    }


}

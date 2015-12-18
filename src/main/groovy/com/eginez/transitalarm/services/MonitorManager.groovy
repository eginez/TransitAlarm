package com.eginez.transitalarm.services

import com.eginez.transitalarm.model.MonitorPreferences
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime
import rx.Observable
import rx.observables.ConnectableObservable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import rx.subjects.Subject

import javax.inject.Inject
import java.util.concurrent.TimeUnit

@CompileStatic
@Singleton
@Slf4j
class MonitorManager {
    @Inject TransitMonitor transitMonitor
    private Map<String, ConnectableObservable> allMonitors = [:]
    private SerializedSubject<Object,ConnectableObservable> monitorStream = new SerializedSubject<>(PublishSubject.<ConnectableObservable>create())
    private List<MonitorPreferences> monitorPreferences = []


    public Observable createMonitor(String routeName, String stopCode, String tripName, LocalDateTime startAt = LocalDateTime.now(),
                                    int duration = 15, TimeUnit unit = TimeUnit.MINUTES){
        def monitor = transitMonitor.startMonitorBusAtStop(startAt, routeName, stopCode, tripName, duration)
        //Start monitoring right away
        monitor.connect()
        monitorStream.onNext(monitor)
        allMonitors.put(routeName+stopCode+tripName, monitor)
    }

    public Observable<ConnectableObservable> getMonitorStream() {
        return monitorStream
    }
    private Observable updateTransitMonitor(String routeName, String stopCode, String tripName, LocalDateTime startAt = LocalDateTime.now(),
                                    int duration = 15, TimeUnit unit = TimeUnit.MINUTES){
        def monitor = transitMonitor.updateMonitorBusAtStop(startAt, routeName, stopCode, tripName, duration)
        monitor.connect()
        monitorStream.onNext(monitor)
        allMonitors.put(routeName+stopCode+tripName, monitor)
    }

    public ConnectableObservable getMonitor(String routeName, String stopCode, String tripName) {
        return allMonitors.get(routeName+stopCode+tripName)
    }


    void saveOrUpdatePreferences(String routeNumber, String stopCode, String direction, LocalDateTime startAt, int durationInMins) {
        def preferences = new MonitorPreferences( routeNumber: routeNumber, stopCode: stopCode, direction: direction,
                startAt: startAt, duration: durationInMins)
        monitorPreferences << preferences
        updateMonitor(routeNumber, stopCode, direction, startAt, durationInMins)
    }

    public Observable updateMonitor(String routeName, String stopCode, String tripName, LocalDateTime startAt = LocalDateTime.now(),
                                    int duration = 15){
        ConnectableObservable obs = getMonitor(routeName, stopCode, tripName)
        if(obs == null) {
            return createMonitor(routeName, stopCode, tripName, startAt, duration)
        } else {
            return updateTransitMonitor(routeName, stopCode, tripName, startAt, duration)
        }
    }

}

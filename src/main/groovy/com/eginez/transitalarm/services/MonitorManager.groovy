package com.eginez.transitalarm.services

import com.eginez.transitalarm.model.MonitorPreferences
import com.google.inject.Singleton
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime
import rx.Observable
import rx.observables.ConnectableObservable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject

import javax.inject.Inject
import javax.inject.Named
import java.util.concurrent.TimeUnit

@CompileStatic
@Singleton
@Slf4j
class MonitorManager {
    @Inject @Named('preferenceFile') private String preferenceFile
    @Inject TransitMonitor transitMonitor
    private Map<String, ConnectableObservable> allMonitors = [:]
    private SerializedSubject<Object,ConnectableObservable> monitorStream = new SerializedSubject<>(PublishSubject.<ConnectableObservable>create())
    Map<String,MonitorPreferences> monitorPreferences = [:]


    private Observable createMonitor(String routeName, String stopCode, String tripName, LocalDateTime startAt = LocalDateTime.now(),
                                    int duration = 15, TimeUnit unit = TimeUnit.MINUTES){
        def monitor = transitMonitor.startMonitorBusAtStop(startAt, routeName, stopCode, tripName, duration)
        //Start monitoring right away
        monitor.connect()
        monitorStream.onNext(monitor)
        allMonitors.put(routeName+stopCode+tripName, monitor)
    }

    private Observable createMonitor(MonitorPreferences preference) {
        return createMonitor(preference.routeNumber, preference.stopCode, preference.direction, preference.startAt, preference.duration)
    }

    public Observable<ConnectableObservable> getMonitorStream() {
        return monitorStream
    }
    private Observable updateTransitMonitor(String routeName, String stopCode, String tripName, LocalDateTime startAt, int duration){
        def monitor = transitMonitor.updateMonitorBusAtStop(startAt, routeName, stopCode, tripName, duration)
        monitor.connect()
        monitorStream.onNext(monitor)
        allMonitors.put(routeName+stopCode+tripName, monitor)
    }

    public ConnectableObservable getMonitor(String routeName, String stopCode, String tripName) {
        return allMonitors.get(routeName+stopCode+tripName)
    }


    void saveOrUpdatePreferences(String routeNumber, String stopCode, String direction, LocalDateTime startAt, int durationInMins = 15) {
        String key =  routeNumber+stopCode+direction.toLowerCase()
        key = key.replace(' ', '')
        def preferences = new MonitorPreferences( routeNumber: routeNumber, stopCode: stopCode, direction: direction,
                startAt: startAt, duration: durationInMins)
        monitorPreferences.put(key, preferences)
        updateMonitor(routeNumber, stopCode, direction, startAt, durationInMins)
    }

    public Observable updateMonitor(String routeName, String stopCode, String tripName, LocalDateTime startAt, int duration){
        ConnectableObservable obs = getMonitor(routeName, stopCode, tripName)
        if(obs == null) {
            return createMonitor(routeName, stopCode, tripName, startAt, duration)
        } else {
            return updateTransitMonitor(routeName, stopCode, tripName, startAt, duration)
        }
    }

    def loadFromPersistence() {
        try {
            def file = new File(preferenceFile)
            log.debug("Loading preferences from ${file.absolutePath}")
            def preferences = new JsonSlurper().parse(file) as Map<String, Map>
            monitorPreferences = preferences.collectEntries { k,it -> [k, new MonitorPreferences( routeNumber: it.routeNumber as String,
                    stopCode: it.stopCode as String, direction: it.direction as String, duration: it.duration as int,
                    startAt: new LocalDateTime().withMillisOfDay(it.startAt as int))]} as Map<String, MonitorPreferences>
            monitorPreferences.each { createMonitor(it.value) }
        }catch(JsonException|IOException ex) {
            log.warn("Can't read from preferences", ex)
        }
    }

    def saveToPersistence() {
        try {
            Map<String, Map> settings = monitorPreferences.collectEntries {k, v -> [k, v.asWritableMap()]} as Map<String, Map>
            def file = new File(preferenceFile)
            file.text = JsonOutput.toJson(settings)
            log.debug("Saving preferences to ${file.absolutePath}")
        }catch(IOException ex) {
            log.warn("Can't save from preferences", ex)
        }
    }
}

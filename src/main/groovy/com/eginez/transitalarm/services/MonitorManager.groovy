package com.eginez.transitalarm.services

import com.google.inject.Singleton
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import rx.Observable
import rx.observables.ConnectableObservable

import javax.inject.Inject
import java.util.concurrent.TimeUnit

@Singleton
class MonitorManager {
     private Map<String, ConnectableObservable> allMonitors = [:]
     @Inject TransitMonitor transitMonitor


     public Observable createMonitor(String routeName, String stopCode, String tripName, DateTime startAt = DateTime.now(),
                                     long duration = 15, TimeUnit unit = TimeUnit.MINUTES){
          def monitor = transitMonitor.startMonitorBusAtStop(startAt, routeName, stopCode, tripName)
          //Start monitoring right away
          //monitor.connect()
          allMonitors.put(routeName+stopCode+tripName, monitor)
     }

     public ConnectableObservable getMonitor(String routeName, String stopCode, String tripName) {
          return allMonitors.get(routeName+stopCode+tripName)
     }
}

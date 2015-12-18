package com.eginez.transitalarm.services

import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Stop
import com.eginez.transitalarm.ui.Notifier
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.Seconds
import rx.Observable
import rx.observables.ConnectableObservable

import javax.inject.Inject
import com.google.inject.Singleton

import javax.inject.Named

@Singleton
@Slf4j
class NotificationsManager {
    private Observable<ConnectableObservable> monitorChangesStream
    private long lastNotificationTimeStamp
    @Inject @Named("notificationFrequency")private Integer notificationFreqencyInMins
    @Inject Notifier notifier

    @Inject
    public NotificationsManager(MonitorManager monitorManager){
        this.monitorChangesStream = monitorManager.monitorStream
        this.monitorChangesStream.subscribe { onNewMonitor(it as ConnectableObservable) }
    }

    private onNewMonitor(ConnectableObservable<List<Tuple2<Stop, ArrivalsAndDepartures>>> connectableObservable) {
        connectableObservable.connect()
        connectableObservable.subscribe { onNewTransitData(it )}
    }

    private onNewTransitData(List<Tuple2<Stop, ArrivalsAndDepartures>> transitData) {
        DateTime lastNotification = new DateTime(lastNotificationTimeStamp)
        long secsSinceLastNotify = Seconds.secondsBetween(lastNotification, DateTime.now()).seconds

        if(secsSinceLastNotify < (notificationFreqencyInMins * 60)){
            log.debug("Wont notify, last notifed time was ${secsSinceLastNotify} ago")
            return
        }
        notify(transitData.first())
    }

    private notify(Tuple2<Stop, ArrivalsAndDepartures> info) {
        lastNotificationTimeStamp = System.currentTimeMillis()
        notifier.notify(info.first.name, info.second.prettyPrint())
    }
}

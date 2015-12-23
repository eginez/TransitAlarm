package com.eginez.transitalarm.model

import org.joda.time.LocalDateTime

class MonitorPreferences implements Serializable {
    String routeNumber
    String stopCode
    String direction
    int duration
    LocalDateTime startAt


    public Map asWritableMap() {
        def map = properties
        map['startAt'] = startAt.millisOfDay
        ['class', 'metaClass'].each {map.remove it}
        return map
    }

}

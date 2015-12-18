package com.eginez.transitalarm.model

import org.joda.time.LocalDateTime

class MonitorPreferences implements Serializable {
    String routeNumber
    String stopCode
    String direction
    int duration
    LocalDateTime startAt
}

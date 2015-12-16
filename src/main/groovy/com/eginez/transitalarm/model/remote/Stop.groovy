package com.eginez.transitalarm.model.remote

import groovy.transform.ToString
import groovy.transform.builder.Builder

@ToString
class Stop {
    String id
    String name
    String code
    String direction
    double lon
    double lat
    String locationType
    List<String> routeIds
    String wheelchairBoarding
}

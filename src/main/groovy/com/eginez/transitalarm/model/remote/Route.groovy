package com.eginez.transitalarm.model.remote

import groovy.transform.ToString
import groovy.transform.builder.Builder

@Builder
@ToString
class Route {
    String agencyId
    String description
    String id
    String longName
    String shortName
    String textColor
    int type
    String url
    String color
    Map<String, List> routeGroups = [:]
}

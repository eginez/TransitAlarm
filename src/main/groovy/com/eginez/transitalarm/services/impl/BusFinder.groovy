package com.eginez.transitalarm.services.impl


import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Route
import com.eginez.transitalarm.model.remote.Stop
import com.eginez.transitalarm.services.remote.PudgetSoundTransitService
import groovy.util.logging.Slf4j

import javax.inject.Inject

@Slf4j
class BusFinder {
    @Inject PudgetSoundTransitService service

    @Inject
    public BusFinder() { }

    public String findBusInfoAlongRoute(String routeName, String stopCode, String tripName) {

        log.debug '----------------------------------------------'
        def routes = service.routesForAgency(PudgetSoundTransitService.AGENCY_ID).execute().body()
                .data.list.collect { new Route(it) }
        def route = routes.find { it.shortName == routeName }
        def stopsData  = service.stopForRoute(route.id).execute().body().data as Map

        stopsData.entry.stopGroupings.stopGroups.each {
            it.each{route.routeGroups[it.name.name]= it.stopIds}
        }
        def stops = stopsData.references.stops.collect { new Stop(it)}
        def stop = stops.find{it.code == stopCode}

        def arr = service.arrivalsForStop(stop.id).execute().body()
        def info = arr.data.entry.arrivalsAndDepartures.collect { new ArrivalsAndDepartures(it)}


        log.debug("At stop ${stop.name}")
        def allBusesAtStop = info.findAll{it.routeShortName == routeName}
        log.debug "Found the following buses"
        allBusesAtStop.each{ log.debug it.prettyPrint() }

        def previousStops = route.routeGroups.get(tripName)[0..allBusesAtStop.first().stopSequence].reverse()
        log.debug("Searching in previouseStops: ${previousStops}")
        def sb = new StringBuilder();
        previousStops.each { sid ->
            def s = stops.find { it.id == sid }
            sb.append("At ${s.name}: ${getInfoForStopAndRoute(service, sid, routeName).prettyPrint()} ")
            sb.append "\n"
        }
        return sb.toString()
    }

    private ArrivalsAndDepartures getInfoForStopAndRoute(PudgetSoundTransitService service, String stopId, String routeName) {
        def arr = service.arrivalsForStop(stopId).execute().body()
        def info = arr.data.entry.arrivalsAndDepartures.collect { new ArrivalsAndDepartures(it)}
        return info.find { it.routeShortName == routeName}
    }
}

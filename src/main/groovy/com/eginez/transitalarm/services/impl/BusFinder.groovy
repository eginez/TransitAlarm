package com.eginez.transitalarm.services.impl


import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Stop
import com.eginez.transitalarm.model.remote.StopGroups
import com.eginez.transitalarm.services.remote.PudgetSoundTransitService
import groovy.util.logging.Slf4j

import javax.inject.Inject

@Slf4j
class BusFinder {
    @Inject PudgetSoundTransitService service

    @Inject
    public BusFinder() { }

    public List<Tuple2<Stop, ArrivalsAndDepartures>> findBusInfoAlongRoute(String routeName, String stopCode, String tripName) {

        log.debug '----------------------------------------------'
        def routes = service.routesForAgency(PudgetSoundTransitService.AGENCY_ID).execute().body()
        def route = routes.find { it.shortName == routeName }
        def stopsData  = service.stopForRoute(route.id).execute().body()

        def stop = stopsData.stops.find {it.code == stopCode}

        def arr = service.arrivalsForStop(stop.id).execute().body()


        log.debug("At stop ${stop.name}")
        def allBusesAtStop = arr.findAll{it.routeShortName == routeName}
        log.debug "Found the following buses"
        allBusesAtStop.each{ log.debug it.prettyPrint() }

        def previousStops = getPreviousStops(stopsData.stopGroups, tripName, allBusesAtStop.first().stopSequence)
        log.debug("Searching in previouseStops: ${previousStops}")
        def response = []
        previousStops.each { String sid ->
            def s = stopsData.stops.find { it.id == sid }
            response << new Tuple2(s, getInfoForStopAndRoute(service, sid, routeName))
        }
        return response
    }

    private List<String> getPreviousStops(Collection<StopGroups> stopGroups, String tripName, int stopSequence) {
        StopGroups group = stopGroups.find { grp -> return tripName in grp.names }
        return group.stopIds[0..stopSequence].reverse()
    }

    private ArrivalsAndDepartures getInfoForStopAndRoute(PudgetSoundTransitService service, String stopId, String routeName) {
        def arr = service.arrivalsForStop(stopId).execute().body()
        return arr.find { it.routeShortName == routeName}
    }
}

package com.eginez.transitalarm.services.remote

import retrofit.Call
import retrofit.http.Field
import retrofit.http.GET
import retrofit.http.Path

interface PudgetSoundTransitService {
    public static final String URL = "http://api.pugetsound.onebusaway.org"
    public static final String AGENCY_ID = '1'
    public static final String KEY='TEST'

    @GET("/api/where/agencies-with-coverage.json?key=TEST")
    Call<Map> getAgencies()

    @GET("/api/where/agency/{id}.json?key=TEST")
    Call<Map> getAgency(@Path('id') String id)

    @GET("api/where/routes-for-agency/{id}.json?key=TEST")
    Call<Map> routesForAgency(@Path('id') String id)

    @GET("api/where/stops-for-route/{routeId}.json?key=TEST&version=2&includePolylines=false")
    Call<Map> stopForRoute(@Path('routeId') String routeId)

    @GET("api/where/arrivals-and-departures-for-stop/{stopId}.json?key=TEST")
    Call<Map> arrivalsForStop(@Path('stopId') String stopId)

}

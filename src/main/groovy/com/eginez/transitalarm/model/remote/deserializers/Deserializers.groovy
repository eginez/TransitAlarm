package com.eginez.transitalarm.model.remote.deserializers

import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Route
import com.eginez.transitalarm.model.remote.RouteStopInformation
import com.eginez.transitalarm.model.remote.Stop
import com.eginez.transitalarm.model.remote.StopGroups
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import org.joda.time.Instant
import org.joda.time.LocalDateTime

import java.lang.reflect.Type

@CompileStatic
class Deserializers {

    static class RouteDeserializer implements Deserializable<Collection<Route>> {
        @Override
        Collection<Route> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            def element = json.getAsJsonObject().get("data").getAsJsonObject().get("list")
            Type type = new TypeToken<Collection<Route>>() {}.getType();
            return (Collection<Route>) new Gson().fromJson(element, type)
        }

        @Override
        Type getType() {
            Type type = new TypeToken<Collection<Route>>() {}.getType();
            return type
        }
    }

    static class StopDeserializer implements Deserializable<Collection<Stop>> {
        @Override
        Collection<Stop> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            def element = json.getAsJsonObject().get("data").getAsJsonObject().get("references").getAsJsonObject().get("stops")
            Type listType = new TypeToken<Collection<Stop>>() {}.getType();
            return new Gson().fromJson(element, listType) as Collection<Stop>
        }

        @Override
        Type getType() {
            Type type = new TypeToken<Collection<Stop>>() {}.getType();
            return type
        }
    }

    static class RouteStopInformationDeserializer implements Deserializable<RouteStopInformation> {
        @Override
        RouteStopInformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            def groups = json.getAsJsonObject().get("data")
                    .getAsJsonObject().get("entry")
                    .getAsJsonObject().get("stopGroupings")
                    .getAsJsonArray().get(0)
                    .getAsJsonObject().get('stopGroups')

            def stops = json.getAsJsonObject().get('data')
                    .getAsJsonObject().get('references')
                    .getAsJsonObject().get('stops')

            RouteStopInformation info = new RouteStopInformation()
            def groupDes = new StopGroupsDeserializer()
            def gson = new GsonBuilder().registerTypeAdapter(groupDes.type, groupDes).create()
            Type type = new TypeToken<Collection<StopGroups>>(){}.getType()
            info.stopGroups = gson.fromJson(groups, type) as Collection<StopGroups>
            type = new TypeToken<Collection<Stop>>(){}.getType()
            info.stops = gson.fromJson(stops, type) as Collection<Stop>
            return info
        }

        @Override
        Type getType() {
            Type type = new TypeToken<RouteStopInformation>() {}.getType();
            return type
        }
    }

    static class StopGroupsDeserializer implements Deserializable<StopGroups> {

        @Override
        Type getType() {
            return new TypeToken<StopGroups>() {}.getType()
        }

        @Override
        StopGroups deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Type type = new TypeToken<StopGroups>(){}.getType()
            def gson = new Gson()
            def stopGroups = gson.fromJson(json, type) as StopGroups
            type = new TypeToken<List<String>>() {}.getType()
            def elem = json.asJsonObject.get('name').asJsonObject.get('names')
            stopGroups.names = gson.fromJson(elem, type) as List<String>
            return stopGroups
        }
    }

    static class ArrivalAndDeparturesDeserializer implements Deserializable<Collection<ArrivalsAndDepartures>> {

        @Override
        Type getType() {
            return new TypeToken<Collection<ArrivalsAndDepartures>>() {}.getType()
        }

        @Override
        Collection<ArrivalsAndDepartures> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            def element = json.getAsJsonObject().get("data")
                    .getAsJsonObject().get("entry")
                    .asJsonObject.get('arrivalsAndDepartures')
            Type type = new TypeToken<Collection<ArrivalsAndDepartures>>() {}.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(new LocalDateTimeDeserializer().type, new LocalDateTimeDeserializer()).create()
            return (Collection<ArrivalsAndDepartures>) gson.fromJson(element, type)
        }
    }

    static class LocalDateTimeDeserializer implements Deserializable<LocalDateTime> {

        @Override
        Type getType() { return new TypeToken<LocalDateTime>() {}.type }

        @Override
        LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new LocalDateTime(json.asLong)
        }
    }
}

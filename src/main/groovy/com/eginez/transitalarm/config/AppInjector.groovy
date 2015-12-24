package com.eginez.transitalarm.config

import com.eginez.transitalarm.model.remote.deserializers.Deserializable
import com.eginez.transitalarm.model.remote.deserializers.Deserializers
import com.eginez.transitalarm.services.MonitorManager
import com.eginez.transitalarm.services.TransitMonitor
import com.eginez.transitalarm.services.impl.BusFinder
import com.eginez.transitalarm.services.remote.PudgetSoundTransitService
import com.eginez.transitalarm.services.impl.PugdetSoundTransitMonitor
import com.eginez.transitalarm.services.NotificationsManager
import com.eginez.transitalarm.ui.MacOSXNotifier
import com.eginez.transitalarm.ui.Notifier
import com.google.gson.GsonBuilder
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Names
import retrofit.Converter
import retrofit.GsonConverterFactory
import retrofit.Retrofit



class AppInjector extends AbstractModule {

    private final static int NOTIFICATION_FREQUENCY = 3 //in mins
    @Override
    protected void configure() {
        bind(BusFinder)
        bind(TransitMonitor).to(PugdetSoundTransitMonitor)
        bind(MonitorManager)
        bind(Integer).annotatedWith(Names.named('notificationFrequency')).toInstance(NOTIFICATION_FREQUENCY)
        bind(String).annotatedWith(Names.named('preferenceFile')).toInstance("${System.getProperty('user.home')}/.transitAlarm.config".toString())
        bind(Notifier).to(MacOSXNotifier)
        bind(NotificationsManager)
    }

    @Provides @Singleton
    PudgetSoundTransitService providesPudgetSoundTransitService(Retrofit retrofit) {
        return retrofit.create(PudgetSoundTransitService.class)
    }

    @Provides @Singleton
    Retrofit providesRetrofit(Converter.Factory factory) {
        Retrofit rt = new Retrofit.Builder()
                .baseUrl(PudgetSoundTransitService.URL)
                .addConverterFactory(factory)
                .build()
        return rt
    }


   // @Provides @Singleton
   // Converter.Factory providesConverterFactory2() {
   //    return JacksonConverterFactory.create()
   // }

    @Provides @Singleton
    Converter.Factory providesConverterFactory(Deserializable[] deserializables) {
        GsonBuilder builder = new GsonBuilder()
        for(Deserializable d : deserializables) {
            builder.registerTypeAdapter(d.type, d)
        }
        return GsonConverterFactory.create(builder.create())
    }

    @Provides @Singleton
    Deserializable[] provideDeserializers() {
        Deserializable[] des = [
                new Deserializers.RouteDeserializer(),
                new Deserializers.StopDeserializer(),
                new Deserializers.ArrivalAndDeparturesDeserializer(),
                new Deserializers.RouteStopInformationDeserializer()]
        return des
    }

    @Provides
    ViewConfig[] providesViewConfigurations() {
        def allViews = []
        allViews << new ViewConfig(name: 'primary', height: 200, width: 200, url: new File('./src/main/resources/main.fxml').toURL())
        allViews << new ViewConfig(name: 'preferences', height: 200, width: 400, url: new File('./src/main/resources/preferences.fxml').toURL())
        return allViews.toArray() as ViewConfig[]
    }
}

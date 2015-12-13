package com.eginez.transitalarm.config

import com.eginez.transitalarm.services.MonitorManager
import com.eginez.transitalarm.services.TransitMonitor
import com.eginez.transitalarm.services.impl.BusFinder
import com.eginez.transitalarm.services.remote.PudgetSoundTransitService
import com.eginez.transitalarm.services.impl.PugdetSoundTransitMonitor
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import retrofit.Converter
import retrofit.GsonConverterFactory
import retrofit.Retrofit


class AppInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(BusFinder)
        bind(TransitMonitor).to(PugdetSoundTransitMonitor)
        bind(MonitorManager)
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

    @Provides @Singleton
    Converter.Factory providesConverterFactory() {
        return GsonConverterFactory.create()
    }

    @Provides
    ViewConfig[] providesViewConfigurations() {
        def allViews = []
        allViews << new ViewConfig(name: 'primary', height: 200, width: 200, url: new File('./src/main/resources/main.fxml').toURL())
        allViews << new ViewConfig(name: 'preferences', height: 200, width: 400, url: new File('./src/main/resources/preferences.fxml').toURL())
        return allViews.toArray() as ViewConfig[]
    }
}

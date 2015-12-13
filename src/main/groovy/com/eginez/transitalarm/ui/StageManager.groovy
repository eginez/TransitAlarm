package com.eginez.transitalarm.ui

import com.eginez.transitalarm.config.ViewConfig
import com.google.inject.Injector
import javafx.fxml.FXMLLoader
import javafx.fxml.JavaFXBuilderFactory
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Callback

import javax.inject.Inject

class StageManager {

    ViewConfig[] allViews
    Map<String, Stage> stages = [:]

    @Inject
    StageManager(ViewConfig[] allViews) {
        this.allViews = allViews
    }

    public loadStages(Injector injector) {
        def guiceFactory = { Class<?> aClass -> injector.getInstance(aClass) } as Callback
        allViews.each{ loadView(it, guiceFactory) }
    }

    public Stage loadView(ViewConfig view, Callback guiceFactory) {
        def loader = new FXMLLoader(view.url, null, new JavaFXBuilderFactory(), guiceFactory)
        Parent root = loader.load()
        def stage = new Stage()
        stage.scene = new Scene(root, view.width, view.height)
        def viewable = loader.getController() as Viewable
        viewable.setStage(stage)
        stages[view.name.toLowerCase()] = stage
    }

    public Stage getPrimaryStage() {
        return stages['primary']
    }
}

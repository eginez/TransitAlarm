package com.eginez.transitalarm.ui

import groovy.transform.CompileStatic
import javafx.stage.Stage
import javafx.stage.WindowEvent

trait Viewable {
    protected Stage stage

    public setStage(Stage st) {
        stage = st
        stage.setOnShown { WindowEvent event -> onShown(event) }
        stage.setOnHidden { WindowEvent event -> onHidden(event) }
    }
    public void onHidden(WindowEvent event) { }

    public void onShown(WindowEvent event) {
        println 'on showing'
    }
}
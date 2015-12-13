package com.eginez.transitalarm.controllers

import com.eginez.transitalarm.services.TransitMonitor
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField

import javax.inject.Inject

class PreferencesController {
    @Inject TransitMonitor transitAlarm

    @FXML private  ProgressIndicator progressIndicator
    @FXML private  TextField routeName
    @FXML private  TextField stopCode
    @FXML private  TextField direction
    private def allFields = [routeName, stopCode, direction]

    public void onDone(ActionEvent actionEvent) {
        println routeName.text
    }
}

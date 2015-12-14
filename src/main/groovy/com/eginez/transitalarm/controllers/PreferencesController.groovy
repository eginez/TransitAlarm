package com.eginez.transitalarm.controllers

import com.eginez.transitalarm.services.TransitMonitor
import com.eginez.transitalarm.ui.Viewable
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField

import javax.inject.Inject

class PreferencesController implements Viewable {
    @Inject TransitMonitor transitAlarm

    @FXML private  ProgressIndicator progressIndicator
    @FXML private  TextField routeName
    @FXML private  TextField stopCode
    @FXML private  TextField direction
    @FXML private TextField hour
    @FXML private ChoiceBox ampm

    public void onDone(ActionEvent actionEvent) {
        println routeName.text
        stage.close()
    }
}

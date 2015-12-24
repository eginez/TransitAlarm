package com.eginez.transitalarm.controllers

import com.eginez.transitalarm.services.MonitorManager
import com.eginez.transitalarm.ui.Viewable
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.stage.WindowEvent
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

import javax.inject.Inject


class PreferencesController implements Viewable {
    @Inject MonitorManager monitorManager
    @FXML private  ProgressIndicator progressIndicator
    @FXML private  TextField routeName
    @FXML private  TextField stopCode
    @FXML private  TextField direction
    @FXML private TextField hour


    @Override
    void onShown(WindowEvent event) {
        if(monitorManager.monitorPreferences.isEmpty()) {
            return
        }
        def preference = monitorManager.monitorPreferences.find { it.key != null}.value
        routeName.text = preference.routeNumber
        stopCode.text = preference.stopCode
        direction.text = preference.direction
        hour.text = preference.startAt.toString('HH:mm')
    }

    public void onDone(ActionEvent actionEvent) {

        String today = DateTimeFormat.forPattern('YYYY/MM/dd').print(LocalDateTime.now())
        LocalDateTime startTime = LocalDateTime.parse("$today ${hour.text}",
                DateTimeFormat.forPattern('YYYY/MM/dd HH:mm'))
        monitorManager.saveOrUpdatePreferences(routeName.text, stopCode.text, direction.text, startTime, duration)
        stage.close()
    }
}

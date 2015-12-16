package com.eginez.transitalarm.controllers

import com.eginez.transitalarm.model.remote.ArrivalsAndDepartures
import com.eginez.transitalarm.model.remote.Stop
import com.eginez.transitalarm.services.MonitorManager
import com.eginez.transitalarm.ui.Viewable
import groovy.transform.CompileStatic
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.stage.WindowEvent
import rx.Subscription

import javax.inject.Inject

@CompileStatic
class MainController implements Viewable {
    @FXML private TextArea textArea
    @Inject MonitorManager monitorManager
    Subscription subscription


    @FXML protected void onReload(ActionEvent event) {
        println event
    }

    @Override
    void onShown(WindowEvent event) {
        def routeName = '26'
        def  stopCode = '6240'
        def tripName = 'EAST GREEN LAKE FREMONT'
        def monitorEvents = monitorManager.getMonitor(routeName, stopCode, tripName)
        monitorEvents.connect()
        subscription = monitorEvents.subscribe { List<Tuple2<Stop, ArrivalsAndDepartures>> allStops ->
            def sb = new StringBuilder()
            allStops.each {
                sb.append("${it.first.name}: ${it.second.prettyPrint()}\n".toString())
            }
            textArea.text = sb.toString()
        }
    }

    @Override
    void onHidden(WindowEvent event) {
        subscription.unsubscribe()
    }
}

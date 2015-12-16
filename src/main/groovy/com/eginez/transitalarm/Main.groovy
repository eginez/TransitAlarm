package com.eginez.transitalarm

import com.eginez.transitalarm.config.AppInjector
import com.eginez.transitalarm.services.MonitorManager
import com.eginez.transitalarm.ui.StageManager
import com.google.inject.Guice
import com.google.inject.Injector
import javafx.application.Platform
import javafx.stage.Stage
import org.apache.log4j.BasicConfigurator
import javafx.application.Application
import org.joda.time.DateTime

import javax.imageio.ImageIO
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon

class Main extends Application {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch(Main, args)
    }

    @Override
    void start(Stage primaryStage) throws Exception {
        def injector = Guice.createInjector(new AppInjector())

        StageManager controller = initStages(injector)
        primaryStage = controller.primaryStage
        primaryStage.title = "TransitAlarm"

        //Do it only on debug mode
        primaryStage.setOnCloseRequest {
            //Platform.exit()
            //System.exit(0)
        }
        Platform.setImplicitExit(false)
        SystemTray.systemTray.add(createTrayIcon(controller))
        startMonitoring(injector)
    }

    void startMonitoring(Injector injector) {
        def manager = injector.getInstance(MonitorManager)
        def routeName = '26'
        def  stopCode = '6240'
        def tripName = 'EAST GREEN LAKE FREMONT'
        DateTime startTime = DateTime.now()//.plusSeconds(10)
        manager.createMonitor(routeName, stopCode, tripName, startTime)
    }

    private StageManager initStages(Injector injector) {
        def controller = injector.getInstance(StageManager.class)
        controller.loadStages(injector)
        return controller
    }

    private TrayIcon createTrayIcon(StageManager controller){
        def icon = ImageIO.read(new File('./src/main/resources/tray_icon.png'))
        return new TrayIcon(icon, "TransitAlarm", createPopupMenu(controller))
    }

    private PopupMenu createPopupMenu(StageManager controller) {
        def popUp = new PopupMenu()
        def menuShow = new MenuItem('Show')
        def menuPreferences = new MenuItem('Preferences')
        def menuExit = new MenuItem('Exit')
        menuShow.addActionListener {
            Platform.runLater {
                controller.primaryStage.show()
            }
        }

        menuPreferences.addActionListener{
            Platform.runLater {
                controller.stages.preferences.show()
            }
        }
        menuExit.addActionListener {
            Platform.exit()
            System.exit(0)
        }
        popUp.add(menuShow)
        popUp.add(menuPreferences)
        popUp.addSeparator()
        popUp.add(menuExit)
        return popUp
    }
}

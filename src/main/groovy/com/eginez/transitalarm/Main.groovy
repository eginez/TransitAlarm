package com.eginez.transitalarm

import com.eginez.transitalarm.config.AppInjector
import com.eginez.transitalarm.services.MonitorManager
import com.eginez.transitalarm.services.NotificationsManager
import com.eginez.transitalarm.ui.StageManager
import com.google.inject.Guice
import com.google.inject.Injector
import javafx.application.Platform
import javafx.stage.Stage
import org.apache.log4j.BasicConfigurator
import javafx.application.Application
import org.joda.time.LocalDateTime

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
        Platform.setImplicitExit(false)
        injector.getInstance(NotificationsManager)
        def monitorManager = injector.getInstance(MonitorManager)
        monitorManager.loadFromPersistence()
        SystemTray.systemTray.add(createTrayIcon(controller, { monitorManager.saveToPersistence() }))
    }

    void startMonitoring(Injector injector) {
        def manager = injector.getInstance(MonitorManager)
        def routeName = '26'
        def  stopCode = '6240'
        def tripName = 'EAST GREEN LAKE FREMONT'
        manager.createMonitor(routeName, stopCode, tripName, LocalDateTime.now())
    }

    private StageManager initStages(Injector injector) {
        def controller = injector.getInstance(StageManager.class)
        controller.loadStages(injector)
        return controller
    }

    private TrayIcon createTrayIcon(StageManager controller, Closure onExit){
        def icon = ImageIO.read(this.class.classLoader.getResource('tray_icon.png'))
        return new TrayIcon(icon, "TransitAlarm", createPopupMenu(controller, onExit))
    }

    private PopupMenu createPopupMenu(StageManager controller, Closure onExit) {
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
            onExit.call()
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

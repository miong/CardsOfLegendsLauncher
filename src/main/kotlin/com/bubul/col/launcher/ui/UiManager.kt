package com.bubul.col.launcher.ui

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.bubul.col.launcher.core.UpdateManagerListener

class UiManager
{
    private lateinit var app  : LwjglApplication
    val ui = UiGame()

    fun startUi() {
        ui.init()
        val config = LwjglApplicationConfiguration().apply {
            title = "CardsOfLegends Launcher"
            width = 800
            height = 480
            undecorated = true
            resizable = false
        }
        app = LwjglApplication(ui, config)
        app.logLevel = Application.LOG_DEBUG
    }

    fun getUpdateManagerListener(type : String) : UpdateManagerListener {
        return object : UpdateManagerListener {

            override  fun onUpdateStart(isInstallation : Boolean)
            {
                ui.uiMainScreen.bt.isDisabled = true
                ui.uiMainScreen.pb.value = 0f
                ui.uiMainScreen.currFile.clear()
                ui.uiMainScreen.currFile.setText("Calculating updates file")
                if (isInstallation)
                    ui.uiMainScreen.pbTitle.setText("Installing "+type)
                else
                    ui.uiMainScreen.pbTitle.setText("Updating "+type)
            }

            override fun onVersioningStart() {
                ui.uiMainScreen.pbTitle.setText("Registering")
            }

            override fun onCopyingStart() {
                ui.uiMainScreen.pbTitle.setText("Copying")
            }

            override fun onFileStart(fname : String)
            {
                ui.uiMainScreen.currFile.clear()
                ui.uiMainScreen.currFile.setText(fname)
            }

            override fun onUpdateProgress(percent : Int)
            {
                try
                {
                    ui.uiMainScreen.pb.value = percent / 100.0f
                }
                catch (e : Exception)
                {
                    //NO-OP
                }
            }

            override fun onUpdateEnd()
            {
                ui.uiMainScreen.currFile.clear()
                if(type == "game") {
                    ui.uiMainScreen.playReadySound()
                    ui.uiMainScreen.currFile.setText("Ready to play")
                    ui.uiMainScreen.bt.isDisabled = false
                } else {
                    ui.uiMainScreen.currFile.setText("Launcher up to date")
                    ui.uiMainScreen.bt.isDisabled = true
                }
            }
            override fun onCancel()
            {

            }
        }

    }

    fun waitReady() {
        while(!ui.uiReady){
            Thread.sleep(10)
        }
    }

    fun exit() {
        ui.exit()
    }
}
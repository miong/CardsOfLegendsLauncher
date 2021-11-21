package com.bubul.col.launcher.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.bubul.col.launcher.core.startGame
import ktx.actors.plusAssign
import ktx.actors.stage
import ktx.app.KtxScreen
import ktx.scene2d.*
import org.slf4j.LoggerFactory

class UiMainScreen(private val game : UiGame) : KtxScreen {

    lateinit var pb : ProgressBar
    lateinit var bt : Button
    lateinit var currFile : Label
    lateinit var pbTitle : Label

    private lateinit var stage : Stage
    private lateinit var rootTable : Table
    private lateinit var closeLabel : Label

    private var readySound : Sound? = null

    private val logger = LoggerFactory.getLogger( this.javaClass.name)

    fun init()
    {
        stage = stage()
        rootTable = scene2d.table {
            setFillParent(true)
            closeLabel = label("X") {
                it.right()
                it.padRight(10f)
                it.padTop(10f)
            }
            row()
            label("Cards of Legends") {
                it.expand(false, true)
                it.left()
                it.top()
                it.padLeft(10f)
            }
            row()
            row()
            pbTitle = label("")
            row()
            pb = progressBar(style = "updatePB") {
                it.expand(true, false).fill(true, false)
                it.height(30f)
                it.padLeft(10f)
                it.padRight(10f)
            }
            row()
            currFile = label("")
            row()
            bt = button {
                label("Play")
                it.width(50f)
                it.height(60f)
                it.padBottom(10f)
                it.padTop(10f)
            }
        }
        rootTable.touchable = Touchable.childrenOnly
        bt.touchable = Touchable.enabled
        bt.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if(!bt.isDisabled) {
                    startGame()
                    game.exit()
                }
            }
        })
        closeLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.exit()
            }
        })
        try {
            val backgroundTex = Texture(game.getRessourcePath("back_image"))
            rootTable.background = TextureRegionDrawable(TextureRegion(backgroundTex))
            readySound = Gdx.audio.newSound(game.getRessourcePath("ready_to_play.mp3"))
        } catch (e : Exception) {
            logger.error("Can't load resources")
            logger.error(e.toString())
        }
    }

    override fun show() {
        stage += rootTable
        Gdx.input.inputProcessor = stage
        super.show()
    }

    fun playReadySound()
    {
        readySound?.play()
    }

    override fun render(delta: Float) {
        stage.draw()
        stage.act()
    }

    override fun hide() {
        stage.clear()
        super.hide()
    }

}
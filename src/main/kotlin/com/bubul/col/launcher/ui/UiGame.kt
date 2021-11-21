package com.bubul.col.launcher.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ktx.style.button
import ktx.style.label
import ktx.style.progressBar
import java.nio.file.Paths

class UiGame : KtxGame<KtxScreen>() {

    lateinit var uiMainScreen : UiMainScreen
    var uiReady = false

    fun init()
    {
        uiMainScreen = UiMainScreen(this)
    }

    override fun create()
    {
        loadDefaultSkin()
        uiMainScreen.init()
        addScreen(uiMainScreen)
        setScreen<UiMainScreen>()
        super.create()
        uiReady = true
    }

    private fun loadDefaultSkin() {
        val mySkin = Skin()
        mySkin.label {
            font = BitmapFont()
            fontColor = Color.WHITE
        }
        mySkin.progressBar("updatePB") {
            background = TextureRegionDrawable(TextureRegion(Texture(Pixmap(100, 20, Pixmap.Format.RGBA8888).apply {
                setColor(Color.RED)
                fill()
            })))
            knobBefore = TextureRegionDrawable(TextureRegion(Texture(Pixmap(100, 20, Pixmap.Format.RGBA8888).apply {
                setColor(Color.BLUE)
                fill()
            })))
        }
        mySkin.button {
            disabled = TextureRegionDrawable(TextureRegion(Texture(Pixmap(100, 20, Pixmap.Format.RGBA8888).apply {
                setColor(Color.RED)
                fill()
            })))

            up = TextureRegionDrawable(TextureRegion(Texture(Pixmap(100, 20, Pixmap.Format.RGBA8888).apply {
                setColor(Color.BLUE)
                fill()
            })))

            down = TextureRegionDrawable(TextureRegion(Texture(Pixmap(100, 20, Pixmap.Format.RGBA8888).apply {
                setColor(Color.CYAN)
                fill()
            })))
        }
        Scene2DSkin.defaultSkin = mySkin
    }

    fun getRessourcePath(resPath : String) : FileHandle?
    {
        return Gdx.files.absolute(Paths.get("../game/launcher_data", resPath).toAbsolutePath().toString())
    }

    fun exit() {
        Gdx.app.exit()
    }

}
package com.oop.game.base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class GameWorld(
    val screenWidth: Float,
    val screenHeight: Float,
    val worldWidth: Float = screenWidth,
    val worldHeight: Float = screenHeight
) : ScreenAdapter() {

    val camera = OrthographicCamera()
    val batch  = SpriteBatch()
    val font   = BitmapFont()

    var offsetX: Float = 0f
    var offsetY: Float = 0f

    private val gameObjects = mutableListOf<GameObject>()

    init {
        camera.setToOrtho(false, screenWidth, screenHeight)
    }

    fun add(obj: GameObject)    { gameObjects.add(obj) }
    fun remove(obj: GameObject) { gameObjects.remove(obj) }
    fun getObjects(): List<GameObject> = gameObjects.toList()

    protected fun updateAllObjects(delta: Float) {
        for (obj in gameObjects) obj.update(delta)
    }

    // isAlive() = false 인 객체를 한꺼번에 제거 (순회 중 삭제 방지)
    protected fun removeDead() {
        val toRemove = mutableListOf<GameObject>()
        for (obj in gameObjects) {
            if (!obj.isAlive()) toRemove.add(obj)
        }
        for (obj in toRemove) gameObjects.remove(obj)
    }

    open fun update(delta: Float) {
        updateAllObjects(delta)
        removeDead()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined

        update(delta)

        batch.begin()
        drawBackground(batch)
        drawAllObjects()
        batch.end()
    }

    protected abstract fun drawBackground(batch: SpriteBatch)

    private fun drawAllObjects() {
        for (obj in gameObjects) {
            val originalX = obj.x
            val originalY = obj.y
            obj.x -= offsetX
            obj.y -= offsetY
            obj.draw(batch)
            obj.x = originalX
            obj.y = originalY
        }
    }

    fun drawTextOnScreen(text: String, x: Float, y: Float, color: Color = Color.WHITE, scale: Float = 1f) {
        batch.projectionMatrix = camera.combined
        font.color = color
        font.data.setScale(scale)
        batch.begin()
        font.draw(batch, text, x, y)
        batch.end()
    }

    fun drawTextInWorld(text: String, worldX: Float, worldY: Float, color: Color = Color.WHITE, scale: Float = 1f) {
        drawTextOnScreen(text, worldX - offsetX, worldY - offsetY, color, scale)
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        for (obj in gameObjects) obj.dispose()
    }
}

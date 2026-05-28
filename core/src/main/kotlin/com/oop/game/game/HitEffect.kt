package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

class HitEffect(
    x: Float,
    y: Float
) : GameObject(x, y, 64f, 64f) {

    private val texture = Texture(Gdx.files.internal("blast.png"))
    private var timer = 0f
    private val duration = 0.25f
    private var alive = true

    override fun update(delta: Float) {
        timer += delta
        if (timer >= duration) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean = alive

    override fun dispose() {
        texture.dispose()
    }
}
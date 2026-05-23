package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

class BombProjectile(
    x: Float,
    y: Float,
    private val worldHeight: Float
) : GameObject(x, y, 24f, 24f) {

    val damage = 3
    private val speed = 350f
    private var alive = true

    private val texture = Texture(Gdx.files.internal("bomb.png"))

    override fun update(delta: Float) {
        y += speed * delta
        if (y > worldHeight) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean = alive

    fun kill() { alive = false }

    override fun dispose() {
        texture.dispose()
    }
}

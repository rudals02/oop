package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

class EnemyBullet(
    x: Float,
    y: Float,
    val damage: Int = 1
) : GameObject(x, y, 12f, 20f) {

    private val speed = 300f
    private var alive = true
    private val texture = Texture(Gdx.files.internal("bullet.png"))

    override fun update(delta: Float) {
        y -= speed * delta
        if (y + height < 0f) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean = alive

    fun kill() {
        alive = false
    }

    override fun dispose() {
        texture.dispose()
    }
}
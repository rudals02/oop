package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

enum class BulletDirection { UP, DOWN, LEFT, RIGHT }

class Bullet(
    x: Float,
    y: Float,
    private val worldHeight: Float,
    private val worldWidth: Float,
    val direction: BulletDirection = BulletDirection.UP,
    val damage: Int = 1
) : GameObject(x, y, 8f, 16f) {

    private val speed = 600f
    private var alive = true
    private val texture = Texture(Gdx.files.internal("bullet.png"))

    override fun update(delta: Float) {
        when (direction) {
            BulletDirection.UP -> y += speed * delta
            BulletDirection.DOWN -> y -= speed * delta
            BulletDirection.LEFT -> x -= speed * delta
            BulletDirection.RIGHT -> x += speed * delta
        }

        if (y > worldHeight || y + height < 0f || x > worldWidth || x + width < 0f) {
            alive = false
        }
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
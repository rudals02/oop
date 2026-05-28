package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class RushEnemy(
    x: Float,
    y: Float,
    private val minX: Float,
    private val maxX: Float
) : Enemy(
    x = x,
    y = y,
    width = 60f,
    height = 45f,
    initialHp = 7,
    score = 300,
    speed = 330f
) {

    private val texture = Texture(Gdx.files.internal("rush_enemy.png"))

    private var dirX = if (Math.random() < 0.5) -1f else 1f
    private var dirY = if (Math.random() < 0.5) -1f else 1f

    private val minY = 70f
    private val maxY = 560f

    override fun update(delta: Float) {
        x += dirX * speed * delta
        y += dirY * speed * delta

        if (x <= minX) {
            x = minX
            dirX = 1f
        }

        if (x + width >= maxX) {
            x = maxX - width
            dirX = -1f
        }

        if (y <= minY) {
            y = minY
            dirY = 1f
        }

        if (y + height >= maxY) {
            y = maxY - height
            dirY = -1f
        }
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}
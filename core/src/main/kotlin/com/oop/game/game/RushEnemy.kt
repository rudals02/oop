package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

/**
 * 돌진 적 — 빠른 속도로 좌우를 왕복하며, 수직으로도 천천히 진동.
 */
class RushEnemy(
    x: Float,
    y: Float,
    private val minX: Float,
    private val maxX: Float
) : GameObject(x, y, 60f, 45f) {

    private val texture = Texture(Gdx.files.internal("rush_enemy.png"))

    private val speedX = 320f
    private val speedY = 60f
    private var dirX = 1f
    private var time = 0f

    private val baseY = y

    override fun update(delta: Float) {
        time += delta

        // 빠른 수평 왕복
        x += speedX * dirX * delta
        if (x <= minX) { x = minX; dirX = 1f }
        else if (x + width >= maxX) { x = maxX - width; dirX = -1f }

        // 사인파 수직 진동
        y = baseY + sin(time * 2f) * speedY
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }

    private fun sin(v: Float) = kotlin.math.sin(v)
}

package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import kotlin.math.cos
import kotlin.math.sin

/**
 * 드론 적 — 중심점을 기준으로 원형 궤도를 돌며 이동.
 */
class DroneEnemy(
    x: Float,
    y: Float
) : GameObject(x, y, 55f, 55f) {

    private val texture = Texture(Gdx.files.internal("drone_enemy.png"))

    private val centerX = x
    private val centerY = y
    private val radius = 90f
    private val angularSpeed = 1.8f  // 라디안/초
    private var angle = 0f

    override fun update(delta: Float) {
        angle += angularSpeed * delta
        x = centerX + cos(angle) * radius
        y = centerY + sin(angle) * radius
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}

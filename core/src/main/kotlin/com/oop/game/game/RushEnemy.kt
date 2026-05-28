package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.sin

/**
 * 돌진 적.
 *
 * DroneEnemy보다 체력과 점수가 높고,
 * 좌우로 빠르게 움직이면서 위아래로 조금 흔들린다.
 */
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
    speed = 220f
) {

    private val texture = Texture(Gdx.files.internal("rush_enemy.png"))

    private var dirX = 1f
    private var time = 0f
    private val baseY = y

    override fun update(delta: Float) {
        time += delta

        x += speed * dirX * delta

        if (x <= minX) {
            x = minX
            dirX = 1f
        } else if (x + width >= maxX) {
            x = maxX - width
            dirX = -1f
        }

        y = baseY + sin(time * 2f) * 60f
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}
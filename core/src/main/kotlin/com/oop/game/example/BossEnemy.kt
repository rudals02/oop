package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import kotlin.math.sin

/**
 * 보스 적 — 크고 느리게 좌우로 사인파 이동.
 */
class BossEnemy(
    x: Float,
    y: Float
) : GameObject(x, y, 90f, 100f) {

    private val texture = Texture(Gdx.files.internal("boss_enemy.png"))

    private val centerX = x
    private val amplitude = 160f
    private val speed = 0.8f  // 라디안/초
    private var time = 0f

    override fun update(delta: Float) {
        time += delta
        x = centerX + sin(time * speed) * amplitude
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}

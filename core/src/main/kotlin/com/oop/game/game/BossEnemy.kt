package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject
import kotlin.math.sin

/**
 * 보스 적 — 크고 느리게 좌우로 사인파 이동.
 */
class BossEnemy(
    x: Float,
    y: Float
) : Enemy(
    x = x,
    y = y,
    width = 90f,
    height = 100f,
    initialHp = 50,
    score = 3000,
    speed = 80f
) {
    private val texture: Texture = run {
        val pixmap = Pixmap(Gdx.files.internal("boss_enemy.png"))
        val bgColor = pixmap.getPixel(0, 0)
        pixmap.blending = Pixmap.Blending.None
        for (px in 0 until pixmap.width) {
            for (py in 0 until pixmap.height) {
                if (pixmap.getPixel(px, py) == bgColor) {
                    pixmap.drawPixel(px, py, 0)
                }
            }
        }
        Texture(pixmap).also { pixmap.dispose() }
    }

    private val centerX = x
    private val amplitude = 160f
    private val moveSpeed = 0.8f
    private var time = 0f

    var fireballTimer = 0f
    val fireballInterval = 1.2f

    override fun update(delta: Float) {
        time += delta
        x = centerX + sin(time * moveSpeed) * amplitude
    }

    fun canShoot(delta: Float): Boolean {
        fireballTimer += delta
        if (fireballTimer >= fireballInterval) {
            fireballTimer = 0f
            return true
        }
        return false
    }

    fun shootFireball(): Fireball {
        return Fireball(x + width / 2f - 28f, y - 56f)
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}

package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.sin

/**
 * 보스 적 — 크고 느리게 좌우로 사인파 이동.
 * 발사 기능이 있으므로 Shooter를 구현한다.
 */
class BossEnemyAI(
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
), Shooter {

    override val shootInterval = 1.2f
    override var shootTimer = 0f
    // canShoot()는 Shooter의 디폴트 메서드를 그대로 상속받아 씀

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

    override fun update(delta: Float) {
        time += delta
        x = centerX + sin(time * moveSpeed) * amplitude
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
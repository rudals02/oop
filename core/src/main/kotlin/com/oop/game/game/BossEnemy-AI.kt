package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.sin

/**
 * 보스 적 — 크고 느리게 좌우로 사인파 이동하며 파이어볼을 발사한다.
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
) {
    override val shootInterval = 1.2f

    private val texture: Texture = loadTextureWithTransparentBackground("boss_enemy.png")

    private val centerX = x
    private val amplitude = 160f
    private val moveSpeed = 0.8f
    private var time = 0f

    override fun update(delta: Float) {
        time += delta
        x = centerX + sin(time * moveSpeed) * amplitude
    }

    /** 발사 타이밍이 되면 true. 부모의 공통 타이머 로직을 그대로 사용. */
    fun canShoot(delta: Float): Boolean = tickShootTimer(delta)

    fun shootFireball(): Fireball = Fireball(x + width / 2f - 28f, y - 56f)

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }

    /** (0,0) 픽셀 색을 배경으로 간주하고 투명 처리한 텍스처를 로드한다. */
    private fun loadTextureWithTransparentBackground(path: String): Texture {
        val pixmap = Pixmap(Gdx.files.internal(path))
        val bgColor = pixmap.getPixel(0, 0)
        pixmap.blending = Pixmap.Blending.None
        for (px in 0 until pixmap.width) {
            for (py in 0 until pixmap.height) {
                if (pixmap.getPixel(px, py) == bgColor) {
                    pixmap.drawPixel(px, py, 0)
                }
            }
        }
        return Texture(pixmap).also { pixmap.dispose() }
    }
}
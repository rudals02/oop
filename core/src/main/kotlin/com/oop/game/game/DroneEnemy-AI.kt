package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * 기본 드론 적 클래스.
 *
 * 좌우로 왕복 이동하며, 일정 주기로 총알을 발사한다.
 */
class DroneEnemyAI(
    x: Float,
    y: Float,
    private val minX: Float,
    private val maxX: Float
) : Enemy(
    x = x,
    y = y,
    width = 48f,
    height = 42f,
    initialHp = 5,
    score = 100,
    speed = 120f
) {
    override val shootInterval = 2.0f

    private val texture = Texture(Gdx.files.internal("drone_enemy.png"))
    private var direction = 1f

    override fun update(delta: Float) {
        x += speed * direction * delta

        when {
            x <= minX -> {
                x = minX
                direction = 1f
            }
            x + width >= maxX -> {
                x = maxX - width
                direction = -1f
            }
        }
    }

    /** 발사 타이밍이 되면 true. 부모의 공통 타이머 로직을 그대로 사용. */
    fun canShoot(delta: Float): Boolean = tickShootTimer(delta)

    fun shoot(): EnemyBullet = EnemyBullet(x + width / 2f - 6f, y - 20f)

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}
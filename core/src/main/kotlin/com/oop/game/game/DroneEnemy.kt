package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * 기본 드론 적 클래스.
 *
 * 좌우로 왕복 이동하며, 일정 주기로 총알을 발사한다.
 * 발사 기능이 있으므로 Shooter를 구현한다.
 */
class DroneEnemy(
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
), Shooter {

    override val shootInterval = 2.0f
    override var shootTimer = 0f

    private val texture = Texture(Gdx.files.internal("drone_enemy.png"))
    private var direction = 1f

    override fun update(delta: Float) {
        x += speed * direction * delta

        if (x <= minX) {
            x = minX
            direction = 1f
        } else if (x + width >= maxX) {
            x = maxX - width
            direction = -1f
        }
    }

    fun shoot(): EnemyBullet = EnemyBullet(x + width / 2f - 6f, y - 20f)

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}
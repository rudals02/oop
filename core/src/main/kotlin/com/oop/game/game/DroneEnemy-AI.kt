package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

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
), Shooter {

    override val shootInterval = 2.0f
    override var shootTimer = 0f
    // canShoot()는 따로 안 적어도 됨 — Shooter의 디폴트 메서드를 그대로 상속받아 씀

    private val texture = Texture(Gdx.files.internal("drone_enemy.png"))
    private var direction = 1f

    override fun update(delta: Float) {
        x += speed * direction * delta

        if (x <= minX) {
            x = minX
            direction = 1f
        }
        if (x + width >= maxX) {
            x = maxX - width
            direction = -1f
        }
    }

    fun shoot(): EnemyBullet {
        return EnemyBullet(x + width / 2f - 6f, y - 20f)
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}
package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * 기본 드론 적 클래스.
 *
 * 현재는 좌우로 움직이는 가장 기본적인 적이다.
 * 나중에 총알 발사 기능이 추가될 수 있지만,
 * 지금 단계에서는 이동, 체력, 충돌 범위, 점수만 설정한다.
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
) {

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

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}

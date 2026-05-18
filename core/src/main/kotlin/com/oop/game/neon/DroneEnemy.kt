package com.oop.game.neon

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
    initialHp = 1,
    score = 100,
    speed = 120f
) {

    // 드론 이미지
    private val texture = Texture(Gdx.files.internal("enemy.png"))

    // 이동 방향
    // 1이면 오른쪽, -1이면 왼쪽
    private var direction = 1f

    /**
     * 매 프레임마다 드론을 좌우로 이동시킨다.
     */
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

    /**
     * 드론 이미지를 화면에 그린다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /**
     * 이미지 자원 정리.
     */
    override fun dispose() {
        texture.dispose()
    }
}
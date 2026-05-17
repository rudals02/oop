package com.oop.game.neon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * 기본 드론 적 클래스.
 *
 * 현재는 가장 단순한 적으로,
 * 좌우로 왔다 갔다 움직이는 기능만 구현한다.
 *
 * HP는 1이라 총알 한 대를 맞으면 죽는다.
 */
class DroneEnemy(
    x: Float,
    y: Float,
    private val minX: Float,    // 왼쪽 이동 한계
    private val maxX: Float     // 오른쪽 이동 한계
) : Enemy(
    x = x,
    y = y,
    width = 55f,
    height = 55f,
    initialHp = 1,
    score = 100,
    speed = 120f
) {

    // 드론 이미지
    // resources 폴더 안에 enemy.png가 있어야 한다.
    private val texture = Texture(Gdx.files.internal("drone_enemy.png"))

    // 이동 방향
    // 1이면 오른쪽, -1이면 왼쪽
    private var direction = 1f

    /**
     * 매 프레임마다 호출되는 이동 처리 함수.
     *
     * delta는 이전 프레임과 현재 프레임 사이의 시간이다.
     * speed * delta를 사용하면 컴퓨터 성능이 달라도 비슷한 속도로 움직인다.
     */
    override fun update(delta: Float) {
        // 현재 방향으로 이동
        x += speed * direction * delta

        // 왼쪽 끝에 닿으면 오른쪽으로 방향 전환
        if (x <= minX) {
            x = minX
            direction = 1f
        }

        // 오른쪽 끝에 닿으면 왼쪽으로 방향 전환
        if (x + width >= maxX) {
            x = maxX - width
            direction = -1f
        }
    }

    /**
     * 화면에 드론 이미지를 그린다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /**
     * 게임 종료 시 이미지 자원을 정리한다.
     */
    override fun dispose() {
        texture.dispose()
    }
}
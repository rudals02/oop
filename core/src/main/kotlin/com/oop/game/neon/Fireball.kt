package com.oop.game.neon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * 보스가 발사하는 파이어볼 클래스.
 *
 * 위에서 아래 방향으로 떨어진다.
 * Player와 충돌하면 NeonWorld에서 player.takeDamage()를 호출하면 된다.
 */
class Fireball(
    x: Float,
    y: Float,
    val damage: Int = 1     // 플레이어에게 줄 데미지
) : GameObject(x, y, 24f, 24f) {

    // 아래로 떨어지는 속도
    private val speed = 200f

    // 살아있는지 여부
    // 화면 밖으로 나가면 false가 된다.
    private var alive = true

    // 임시로 enemy.png 사용
    // 나중에 fireball.png가 생기면 파일명만 바꾸면 된다.
    private val texture = Texture(Gdx.files.internal("enemy.png"))

    /**
     * 매 프레임마다 아래쪽으로 이동한다.
     */
    override fun update(delta: Float) {
        y -= speed * delta

        // 화면 아래로 완전히 나가면 제거 대상으로 표시
        if (y + height < 0f) {
            alive = false
        }
    }

    /**
     * 파이어볼 이미지를 화면에 그린다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /**
     * GameWorld의 removeDead()에서 사용된다.
     */
    override fun isAlive(): Boolean {
        return alive
    }

    /**
     * 이미지 자원 정리.
     */
    override fun dispose() {
        texture.dispose()
    }
}
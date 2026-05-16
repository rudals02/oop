package com.oop.game.neon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * 보스가 발사하는 탄막.
 *
 * 담당: 이경민 (이동, 제거 조건 구현)
 * 사용: 설재우 (NeonWorld 에서 Player 와 충돌 시 player.takeDamage() 호출)
 *
 * NeonWorld 와의 약속 (변경 금지):
 *   damage, isAlive()
 */
class Fireball(
    x: Float,
    y: Float,
    val damage: Int = 1
) : GameObject(x, y, 24f, 24f) {

    private val speed = 200f
    private var alive = true

    private val texture = Texture(Gdx.files.internal("enemy.png")) // TODO: Fireball 이미지로 교체

    override fun update(delta: Float) {
        y -= speed * delta      // 아래 방향으로 이동
        if (y + height < 0f) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    // 화면 밖으로 나가면 false → NeonWorld 의 removeDead() 가 정리
    override fun isAlive(): Boolean = alive

    override fun dispose() {
        texture.dispose()
    }
}

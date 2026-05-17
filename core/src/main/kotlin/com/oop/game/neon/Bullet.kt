package com.oop.game.neon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * 플레이어가 발사하는 총알.
 *
 * 담당: 최솔잎 (이동, 제거 조건 구현)
 * 사용: 설재우 (NeonWorld 에서 damage 를 enemy.takeDamage() 에 전달)
 *
 * NeonWorld 와의 약속 (변경 금지):
 *   damage, isAlive()
 */
class Bullet(
    x: Float,
    y: Float,
    private val worldHeight: Float,
    private val damage: Int = 1
) : GameObject(x, y, 8f, 16f) {

    private val speed = 600f
    private var alive = true

    private val texture = Texture(Gdx.files.internal("player.png")) // TODO: 총알 이미지로 교체

    override fun update(delta: Float) {
        y += speed * delta
        if (y > worldHeight) alive = false
    }

    override fun draw(batch: SpriteBatch, texture: Texture) {
        batch.draw(texture, x, y, width, height)
    }

    // 화면 밖으로 나가거나 적에게 맞으면 false → NeonWorld 의 removeDead() 가 정리
    override fun isAlive(): Boolean = alive

    /** 적에게 맞았을 때 NeonWorld 가 호출해서 총알을 제거. */
    fun kill() { alive = false }

    override fun dispose() {
        texture.dispose()
    }
}

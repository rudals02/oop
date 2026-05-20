package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

/**
 * 플레이어가 발사하는 총알.
 *
 * 담당: 최솔잎 (이동, 제거 조건 구현)
 * 사용: 설재우 (MainWorld 에서 damage 를 enemy.takeDamage() 에 전달)
 *
 * MainWorld 와의 약속 (변경 금지):
 *   damage, isAlive()
 */
class Bullet(
    x: Float,
    y: Float,
    private val worldHeight: Float,
    val damage: Int = 1
) : GameObject(x, y, 8f, 16f) {

    private val speed = 600f
    private var alive = true

    private val texture = Texture(Gdx.files.internal("bullet.png"))

    override fun update(delta: Float) {
        y += speed * delta
        if (y > worldHeight) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean = alive

    fun kill() { alive = false }

    override fun dispose() {
        texture.dispose()
    }
}

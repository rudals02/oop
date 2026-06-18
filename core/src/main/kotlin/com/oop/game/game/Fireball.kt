package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

/**
 * 보스가 발사하는 파이어볼 클래스.
 *
 * 위에서 아래 방향으로 떨어진다.
 * Player와 충돌하면 MainWorld에서 player.takeDamage()를 호출하면 된다.
 */
class Fireball(
    x: Float,
    y: Float,
    val damage: Int = 2
) : GameObject(x, y, 56f, 56f) {

    private val speed = 260f
    private var alive = true
    private val texture = Texture(Gdx.files.internal("fireball.png")) //이미지 수정하였습니다

    override fun update(delta: Float) {
        y -= speed * delta
        if (y + height < 0f) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean = alive

    fun kill() {
        alive = false
    }

    override fun dispose() {
        texture.dispose()
    }
}
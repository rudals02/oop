package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

/**
 * 드론 적이 발사하는 총알.
 */
class EnemyBulletAI(
    x: Float,
    y: Float,
    val damage: Int = 1
) : GameObject(x, y, width = 10f, height = 15f) {

    private val speed = 300f
    private var alive = true
    // TODO: 텍스처가 "drone.png"로 되어있음 — 총알 전용 스프라이트가 맞는지 확인 필요
    private val texture = Texture(Gdx.files.internal("drone.png"))

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
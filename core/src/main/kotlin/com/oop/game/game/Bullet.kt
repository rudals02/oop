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
    val isDown: Boolean = false,
    val damage: Int = 1
) : GameObject(x, y, 8f, 16f) {

    private val speed = 600f
    private var alive = true

    private val texture = Texture(Gdx.files.internal("bullet.png"))

    override fun update(delta: Float) {
        if (isDown) {
            y -= speed * delta//아래 전진 추가
        } else { y += speed * delta }//위로 전진

        if (isDown){
            if (y + height <0f) alive = false//바닥 뚫고 나가면 죽음
        } else {
            if ( y > worldHeight) alive = false//천장 뚫고 나가면 죽음
        }

    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean {
        return if (isDown) {
            y + height > 0f
        }else{
            y in 0f ..worldHeight
        }
    }

    fun kill() { alive = false }

    override fun dispose() {
        texture.dispose()
    }
}

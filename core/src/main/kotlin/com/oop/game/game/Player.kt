package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject
import com.oop.game.base.InputHandler

/**
 * 플레이어 클래스.
 *
 * 담당: 최솔잎 (이동, 발사, 피격, 폭탄 로직 구현)
 * 사용: 설재우 (MainWorld 에서 hp, bombs, takeDamage, isDead, heal, useBomb 호출)
 *
 * MainWorld 와의 약속 (메서드명 변경 금지):
 *   hp, bombs, takeDamage(), isDead(), heal(), useBomb()
 */
class Player(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
) : GameObject(x, y, 48f, 48f) {

    val maxHp: Int = 3
    var hp: Int = maxHp
        private set
    private var Alivestate = true
    override fun isAlive(): Boolean {
        return hp > 0 && Alivestate
    }

    val bullets = mutableListOf<Bullet>()

    var bombs: Int = 3
        private set

    private val speed = 200f

    private var invincibleTimer = 0f
    private val attackspeed = 0.5f
    private var cooltime = 0f
    val isInvincible: Boolean get() = invincibleTimer > 0f

    private val texture = Texture(Gdx.files.internal("player1.png"))

    override fun update(delta: Float) {
        if (InputHandler.isKeyPressed(InputHandler.LEFT)) x -= speed * delta
        if (InputHandler.isKeyPressed(InputHandler.RIGHT)) x += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.UP)) y += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.DOWN)) y -= speed * delta

        if (cooltime > 0f) cooltime -= delta

        if (InputHandler.isKeyPressed(InputHandler.SPACE) && cooltime <= 0f) {
            shoot()
            cooltime = attackspeed
        }

        if (InputHandler.isKeyPressed(InputHandler.Z)) {
            useBomb()
        }

        if (invincibleTimer > 0f) invincibleTimer -= delta
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)
        bullets.forEach { it.update(delta) }
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }

    fun takeDamage() {
        if (isAlive() == false) return
        if (isInvincible) return
        hp -= 1
        invincibleTimer = 1.5f

        if (hp <= 0) {
            hp = 0
            Alivestate = false
            println("GAME OVER")
        }
    }

    fun isDead(): Boolean {
        return hp <= 0 || isAlive() == false
    }

    fun heal() {
        if (hp < maxHp) hp += 1
    }

    fun shoot() {
        val bulletStartX = this.x + width / 2 - 4f
        val bulletStartY = this.y + height
        bullets.add(Bullet(bulletStartX, bulletStartY, worldHeight))
    }

    fun useBomb(): Boolean {
        if (bombs <= 0) return false
        bombs -= 1
        return true
    }
}

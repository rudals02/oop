package com.oop.game.neon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import com.oop.game.InputHandler

/**
 * 플레이어 클래스.
 *
 * 담당: 최솔잎 (이동, 발사, 피격, 폭탄 로직 구현)
 * 사용: 설재우 (NeonWorld 에서 hp, bombs, takeDamage, isDead, heal, useBomb 호출)
 *
 * NeonWorld 와의 약속 (메서드명 변경 금지):
 *   hp, bombs, takeDamage(), isDead(), heal(), useBomb()
 */
class Player(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
) : GameObject(x, y, 48f, 48f) {

    // ── NeonWorld 가 HUD 에 표시하기 위해 읽는 값 ──
    val maxHp: Int = 3
    var hp: Int = maxHp
        private set

    val bullets = mutableListOf<Bullet>()

    var bombs: Int = 3
        private set

    private val speed = 200f

    // 무적 시간 (피격 후 1.5초)
    private var invincibleTimer = 0f
    private val attackspeed = 0.5f
    private var cooltime = 0f//공속 추가
    val isInvincible: Boolean get() = invincibleTimer > 0f

    private val texture = Texture(Gdx.files.internal("player1.png"))

    // ── 최솔잎 구현 영역 ──

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

    // ── NeonWorld 가 호출하는 메서드 (이름 변경 금지) ──

    /** 적 또는 Fireball 과 충돌 시 NeonWorld 가 호출. 무적 중이면 무시. */
    fun takeDamage() {
        if (isInvincible) return
        hp -= 1
        invincibleTimer = 1.5f
    }

    /** HP 가 0 이하이면 true. NeonWorld 가 GAME_OVER 전환 판정에 사용. */
    fun isDead(): Boolean = hp <= 0

    /** 회복 아이템 획득 시 NeonWorld 가 호출. maxHp 초과 불가. */
    fun heal() {
        if (hp < maxHp) hp += 1
    }

    //bullet 사용 추가
    fun shoot() {
        val bulletStartX = this.x + width / 2 - 4f
        val bulletStartY = this.y + height
        bullets.add(Bullet(bulletStartX, bulletStartY, worldHeight))
    }

    /**
     * 폭탄 사용 시 NeonWorld 가 호출.
     * bombs 가 0 이면 false 반환 (사용 불가).
     * bombs 가 남아있으면 1 감소 후 true 반환.
     */
    fun useBomb(): Boolean {
        if (bombs <= 0) return false
        bombs -= 1
        return true
    }
}

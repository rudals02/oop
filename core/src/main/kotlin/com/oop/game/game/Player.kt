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
    val bombProjectiles = mutableListOf<BombProjectile>()

    var bombs: Int = 0
        private set

    private val speed = 200f

    private var invincibleTimer = 0f
    private val attackspeed = 0.5f
    private var cooltime = 0f
    val isInvincible: Boolean get() = invincibleTimer > 0f

    private val texture = Texture(Gdx.files.internal("player1.png"))
    private val texture_hurt = Texture(Gdx.files.internal("player_hurt.png"))
    private val texture1 = Texture(Gdx.files.internal("player2.png"))
    private val texture2 = Texture(Gdx.files.internal("player3.png"))
    private var currentTexture = texture

    private var animationTimer = 0f
    private val animationSpeed = 0.2f
    private var currentFrame = 1


    override fun update(delta: Float) {
        if (InputHandler.isKeyPressed(InputHandler.LEFT)) x -= speed * delta
        if (InputHandler.isKeyPressed(InputHandler.RIGHT)) x += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.UP)) {
            y += speed * delta
            if (!isInvincible){
                animationTimer += delta
                if (animationTimer >= animationSpeed){
                    animationTimer = 0f
                    currentFrame = if (currentFrame == 1) 2 else 1
                }
                    currentTexture = if (currentFrame == 1)
                        texture1 else texture2}
                    } else{
                        animationTimer = 0f
            currentFrame =1
                    }



        if (InputHandler.isKeyPressed(InputHandler.DOWN)) y -= speed * delta

        if (cooltime > 0f) cooltime -= delta

        if (InputHandler.isKeyPressed(InputHandler.SPACE) && cooltime <= 0f) {
            shoot()
            cooltime = attackspeed
        }

        if (InputHandler.isKeyPressed(InputHandler.Z) && cooltime <= 0f) {
            shootDown()
            cooltime = attackspeed
        }

        if (invincibleTimer > 0f) {
            invincibleTimer -= delta
            if (invincibleTimer <= 0f) {
                invincibleTimer = 0f
                currentTexture = texture
            }
        }
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)
        bullets.forEach { it.update(delta) }
        bombProjectiles.forEach { it.update(delta) }

    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(currentTexture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
        texture1.dispose()
        texture2.dispose()
        texture_hurt.dispose()
    }

    fun takeDamage() {
        if (isAlive() == false) return
        if (isInvincible) return
        hp -= 1
        invincibleTimer = 1.5f
        currentTexture = texture_hurt

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

    fun shootDown(){
        val bulletStartX = this.x + width / 2 - 4f
        val bulletStartY = this.y - 16f
        bullets.add(Bullet(bulletStartX, bulletStartY, worldHeight, isDown=true))
    }

    fun useBomb(): Boolean {
        if (bombs <= 0) return false
        bombs -= 1
        val bombX = x + width / 2f - 12f
        val bombY = y + height
        bombProjectiles.add(BombProjectile(bombX, bombY, worldHeight))
        return true
    }

    fun addBomb() {
        if (bombs < 3) bombs += 1
    }
}

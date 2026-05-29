package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject
import com.oop.game.base.InputHandler

class Player(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
) : GameObject(x, y, 48f, 48f) {

    val maxHp: Int = 3

    var hp: Int = maxHp
        private set

    private var aliveState = true

    override fun isAlive(): Boolean {
        return hp > 0 && aliveState
    }

    val bullets = mutableListOf<Bullet>()
    val bombProjectiles = mutableListOf<BombProjectile>()

    var bombs: Int = 0
        private set

    private val speed = 200f

    private var invincibleTimer = 0f
    private val attackSpeed = 0.5f
    private var cooltime = 0f

    val isInvincible: Boolean
        get() = invincibleTimer > 0f

    private val texture = Texture(Gdx.files.internal("player1.png"))
    private val textureHurt = Texture(Gdx.files.internal("player_hurt.png"))
    private val texture1 = Texture(Gdx.files.internal("player2.png"))
    private val texture2 = Texture(Gdx.files.internal("player3.png"))

    private var currentTexture = texture

    private var animationTimer = 0f
    private val animationSpeed = 0.2f
    private var currentFrame = 1

    override fun update(delta: Float) {
        if (InputHandler.isKeyPressed(InputHandler.LEFT)) {
            x -= speed * delta
        }

        if (InputHandler.isKeyPressed(InputHandler.RIGHT)) {
            x += speed * delta
        }

        if (InputHandler.isKeyPressed(InputHandler.UP)) {
            y += speed * delta

            if (!isInvincible) {
                animationTimer += delta

                if (animationTimer >= animationSpeed) {
                    animationTimer = 0f
                    currentFrame = if (currentFrame == 1) 2 else 1
                }

                currentTexture = if (currentFrame == 1) texture1 else texture2
            }
        } else {
            animationTimer = 0f
            currentFrame = 1

            if (!isInvincible) {
                currentTexture = texture
            }
        }

        if (InputHandler.isKeyPressed(InputHandler.DOWN)) {
            y -= speed * delta
        }

        if (cooltime > 0f) {
            cooltime -= delta
        }

        if (InputHandler.isKeyPressed(InputHandler.SPACE) && cooltime <= 0f) {
            shootUp()
            cooltime = attackSpeed
        }

        if (InputHandler.isKeyPressed(InputHandler.S) && cooltime <= 0f) {
            shootDown()
            cooltime = attackSpeed
        }

        if (InputHandler.isKeyPressed(InputHandler.A) && cooltime <= 0f) {
            shootLeft()
            cooltime = attackSpeed
        }

        if (InputHandler.isKeyPressed(InputHandler.D) && cooltime <= 0f) {
            shootRight()
            cooltime = attackSpeed
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
        bullets.removeAll { !it.isAlive() }

        bombProjectiles.forEach { it.update(delta) }
        bombProjectiles.removeAll { !it.isAlive() }
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(currentTexture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
        textureHurt.dispose()
        texture1.dispose()
        texture2.dispose()
    }

    fun takeDamage(): Boolean {
        if (!isAlive()) return false
        if (isInvincible) return false

        hp -= 1
        invincibleTimer = 1.5f
        currentTexture = textureHurt

        if (hp <= 0) {
            hp = 0
            aliveState = false
            println("GAME OVER")
        }

        return true
    }

    fun isDead(): Boolean {
        return hp <= 0 || !isAlive()
    }

    fun heal() {
        if (hp < maxHp) {
            hp += 1
        }
    }

    fun shootUp() {
        bullets.add(
            Bullet(
                x + width / 2f - 4f,
                y + height,
                worldHeight,
                worldWidth,
                BulletDirection.UP
            )
        )
    }

    fun shootDown() {
        bullets.add(
            Bullet(
                x + width / 2f - 4f,
                y - 16f,
                worldHeight,
                worldWidth,
                BulletDirection.DOWN
            )
        )
    }

    fun shootLeft() {
        bullets.add(
            Bullet(
                x - 8f,
                y + height / 2f,
                worldHeight,
                worldWidth,
                BulletDirection.LEFT
            )
        )
    }

    fun shootRight() {
        bullets.add(
            Bullet(
                x + width,
                y + height / 2f,
                worldHeight,
                worldWidth,
                BulletDirection.RIGHT
            )
        )
    }

    fun useBomb(): Boolean {
        if (bombs <= 0) return false

        bombs -= 1

        val bombX = x + width / 2f - 12f
        val bombY = y + height

        bombProjectiles.add(
            BombProjectile(
                bombX,
                bombY,
                worldHeight
            )
        )

        return true
    }

    fun addBomb() {
        if (bombs < 3) {
            bombs += 1
        }
    }
}
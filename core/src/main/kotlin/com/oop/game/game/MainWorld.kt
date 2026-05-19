package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameWorld

/**
 * 슈팅 게임의 메인 월드.
 *
 * 담당: 설재우
 */
class MainWorld(
    screenWidth: Float,
    screenHeight: Float
) : GameWorld(screenWidth, screenHeight) {

    enum class GameState { PLAYING, GAME_OVER }
    private var state = GameState.PLAYING

    private val player = Player(
        x = screenWidth / 2f - 24f,
        y = 80f,
        worldWidth = screenWidth,
        worldHeight = screenHeight
    )

    private val enemies = mutableListOf<Enemy>()
    var score = 0
        private set

    private val bgTexture = Texture(Gdx.files.internal("background.png"))

    init {
        add(player)
        spawnDrone(screenWidth * 0.25f, screenHeight - 150f)
        spawnDrone(screenWidth * 0.75f, screenHeight - 200f)
    }

    private fun spawnDrone(x: Float, y: Float) {
        val drone = DroneEnemy(x, y, minX = 0f, maxX = screenWidth)
        enemies.add(drone)
        add(drone)
    }

    override fun update(delta: Float) {
        when (state) {
            GameState.PLAYING   -> updatePlaying(delta)
            GameState.GAME_OVER -> Unit
        }
    }

    private fun updatePlaying(delta: Float) {
        updateAllObjects(delta)

        val bulletsToKill = mutableListOf<Bullet>()
        val enemiesToKill = mutableListOf<Enemy>()

        for (bullet in player.bullets) {
            for (enemy in enemies) {
                if (!enemy.isDead() && bullet.isAlive() && bullet.collidesWith(enemy)) {
                    bullet.kill()
                    enemy.takeDamage(1)
                    bulletsToKill.add(bullet)
                    if (enemy.isDead()) enemiesToKill.add(enemy)
                }
            }
        }

        for (enemy in enemiesToKill) {
            score += enemy.score
            enemies.remove(enemy)
            remove(enemy)
        }

        player.bullets.removeAll { !it.isAlive() }

        for (enemy in enemies) {
            if (!enemy.isDead() && player.collidesWith(enemy)) {
                player.takeDamage()
            }
        }

        if (player.isDead()) state = GameState.GAME_OVER

        removeDead()
    }

    override fun drawBackground(batch: SpriteBatch) {
        batch.draw(bgTexture, 0f, 0f, screenWidth, screenHeight)
    }

    override fun render(delta: Float) {
        super.render(delta)

        batch.begin()
        for (bullet in player.bullets) {
            if (bullet.isAlive()) bullet.draw(batch)
        }
        batch.end()

        drawHud()

        if (state == GameState.GAME_OVER) {
            drawTextOnScreen("GAME OVER", screenWidth / 2f - 70f, screenHeight / 2f, Color.RED, 2f)
        }
    }

    private fun drawHud() {
        val topY = screenHeight - 12f

        batch.begin()
        font.data.setScale(1.2f)

        font.color = Color.RED
        font.draw(batch, "HP : ${player.hp}", 12f, topY)

        font.color = Color.YELLOW
        font.draw(batch, "BOMB : ${player.bombs}", screenWidth - 130f, topY)

        font.color = Color.WHITE
        font.draw(batch, "SCORE : $score", screenWidth / 2f - 50f, topY)

        batch.end()
    }

    override fun dispose() {
        super.dispose()
        bgTexture.dispose()
    }
}

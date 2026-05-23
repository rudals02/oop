package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameWorld
import com.oop.game.base.InputHandler

/**
 * 슈팅 게임의 메인 월드.
 *
 * 담당: 설재우
 */
class MainWorld(
    screenWidth: Float,
    screenHeight: Float
) : GameWorld(screenWidth, screenHeight) {

    enum class GameState { STAGE_START, PLAYING, STAGE_CLEAR, GAME_OVER }
    private var state = GameState.STAGE_START
    private var transitionTimer = 0f
    private val clearDuration  = 2.0f
    private val startDuration  = 1.5f

    private val player = Player(
        x = screenWidth / 2f - 24f,
        y = 80f,
        worldWidth = screenWidth,
        worldHeight = screenHeight
    )

    private val enemies = mutableListOf<Enemy>()
    private val items = mutableListOf<Item>()
    private var itemSpawnTimer = 0f
    private val itemSpawnInterval = 3f

    var score = 0
        private set

    private var stage = 1
    private val bgStage1 = Texture(Gdx.files.internal("stage1.png"))
    private val bgStage2 = Texture(Gdx.files.internal("stage2.png"))

    private val hudHpTex    = Texture(Gdx.files.internal("hp.png"))
    private val hudBombTex  = Texture(Gdx.files.internal("bomb_background.png"))
    private val hudScoreTex = Texture(Gdx.files.internal("score.png"))
    private val hudTimerTex = Texture(Gdx.files.internal("timer.png"))

    private var timeLeft = 90f

    init {
        add(player)
        spawnDrone(screenWidth * 0.10f, screenHeight - 150f)
        spawnDrone(screenWidth * 0.30f, screenHeight - 200f)
        spawnDrone(screenWidth * 0.50f, screenHeight - 150f)
        spawnDrone(screenWidth * 0.70f, screenHeight - 200f)
        spawnDrone(screenWidth * 0.90f, screenHeight - 150f)
    }

    private fun spawnRandomItem() {
        val x = (Math.random() * (screenWidth - 32f)).toFloat()
        val type = if (Math.random() < 0.5) ItemType.HEAL else ItemType.BOMB
        items.add(Item(x, screenHeight, type))
    }

    private fun spawnStage2() {
        val positions = listOf(0.10f, 0.25f, 0.40f, 0.55f, 0.70f, 0.82f, 0.92f)
        positions.forEachIndexed { i, xRatio ->
            val y = screenHeight - 150f - (i % 3) * 60f
            spawnRush(screenWidth * xRatio, y)
        }
    }

    private fun spawnRush(x: Float, y: Float) {
        val rush = RushEnemy(x, y, minX = 0f, maxX = screenWidth)
        enemies.add(rush)
        add(rush)
    }

    private fun spawnDrone(x: Float, y: Float) {
        val drone = DroneEnemy(x, y, minX = 0f, maxX = screenWidth)
        enemies.add(drone)
        add(drone)
    }

    override fun update(delta: Float) {
        when (state) {
            GameState.STAGE_START  -> updateStageStart(delta)
            GameState.PLAYING      -> updatePlaying(delta)
            GameState.STAGE_CLEAR  -> updateStageClear(delta)
            GameState.GAME_OVER    -> Unit
        }
    }

    private fun updateStageStart(delta: Float) {
        transitionTimer += delta
        if (transitionTimer >= startDuration) {
            transitionTimer = 0f
            state = GameState.PLAYING
        }
    }

    private fun updateStageClear(delta: Float) {
        transitionTimer += delta
        if (transitionTimer >= clearDuration + startDuration) {
            stage = 2
            spawnStage2()
            transitionTimer = 0f
            state = GameState.PLAYING
        }
    }

    private fun updatePlaying(delta: Float) {
        updateAllObjects(delta)

        val enemiesToKill = mutableListOf<Enemy>()

        for (bullet in player.bullets) {
            for (enemy in enemies) {
                if (!enemy.isDead() && bullet.isAlive() && bullet.collidesWith(enemy)) {
                    bullet.kill()
                    enemy.takeDamage(bullet.damage)
                    if (enemy.isDead()) enemiesToKill.add(enemy)
                }
            }
        }

        if (InputHandler.isKeyJustPressed(InputHandler.Z)) player.useBomb()

        for (bomb in player.bombProjectiles) {
            for (enemy in enemies) {
                if (!enemy.isDead() && bomb.isAlive() && bomb.collidesWith(enemy)) {
                    bomb.kill()
                    enemy.takeDamage(bomb.damage)
                    if (enemy.isDead()) enemiesToKill.add(enemy)
                }
            }
        }
        player.bombProjectiles.removeAll { !it.isAlive() }

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

        items.forEach { it.update(delta) }

        itemSpawnTimer += delta
        if (itemSpawnTimer >= itemSpawnInterval) {
            itemSpawnTimer = 0f
            spawnRandomItem()
        }

        val itemsToCollect = items.filter { player.collidesWith(it) }
        for (item in itemsToCollect) {
            when (item.type) {
                ItemType.HEAL -> player.heal()
                ItemType.BOMB -> player.addBomb()
            }
            item.collect()
        }
        items.removeAll { !it.isAlive() }

        timeLeft -= delta
        if (timeLeft <= 0f) {
            timeLeft = 0f
            state = GameState.GAME_OVER
        }

        if (stage == 1 && enemies.isEmpty()) {
            state = GameState.STAGE_CLEAR
            transitionTimer = 0f
        }

        if (player.isDead()) state = GameState.GAME_OVER

        removeDead()
    }

    override fun drawBackground(batch: SpriteBatch) {
        val bg = if (stage >= 2) bgStage2 else bgStage1
        batch.draw(bg, 0f, 0f, screenWidth, screenHeight)
    }

    override fun render(delta: Float) {
        super.render(delta)

        batch.begin()
        for (bullet in player.bullets) {
            if (bullet.isAlive()) bullet.draw(batch)
        }
        for (bomb in player.bombProjectiles) {
            if (bomb.isAlive()) bomb.draw(batch)
        }
        for (item in items) {
            if (item.isAlive()) item.draw(batch)
        }
        batch.end()

        drawHud()

        if (state == GameState.STAGE_START) {
            val flash = (Math.sin(transitionTimer * 6.0) * 0.4 + 0.6).toFloat()
            val color = Color(0.3f, 1f, 0.5f, flash)
            drawTextOnScreen("STAGE 1 START!", screenWidth / 2f - 95f, screenHeight / 2f, color, 1.6f)
        }

        if (state == GameState.STAGE_CLEAR) {
            val flash = (Math.sin(transitionTimer * 6.0) * 0.4 + 0.6).toFloat()
            if (transitionTimer < clearDuration) {
                val color = Color(1f, 0.85f, 0f, flash)
                drawTextOnScreen("STAGE 1 CLEAR!", screenWidth / 2f - 95f, screenHeight / 2f, color, 1.6f)
            } else {
                val color = Color(0.3f, 1f, 0.5f, flash)
                drawTextOnScreen("STAGE 2 START!", screenWidth / 2f - 95f, screenHeight / 2f, color, 1.6f)
            }
        }

        if (state == GameState.GAME_OVER) {
            drawTextOnScreen("GAME OVER", screenWidth / 2f - 70f, screenHeight / 2f, Color.RED, 2f)
        }
    }

    private fun drawHud() {
        val iconSize = 28f
        val iconY = screenHeight - iconSize - 6f
        val textY = screenHeight - 12f
        val gap = 4f

        // 4개 섹션을 균등하게 배치
        val sectionW = screenWidth / 4f
        val hpX     = sectionW * 0f + 6f
        val scoreX  = sectionW * 1f + 4f
        val timerX  = sectionW * 2f + 4f
        val bombX   = sectionW * 3f + 4f

        batch.begin()
        font.data.setScale(0.95f)
        font.color = Color.WHITE

        // HP
        batch.draw(hudHpTex, hpX, iconY, iconSize, iconSize)
        font.draw(batch, "x${player.hp}", hpX + iconSize + gap, textY)

        // SCORE
        batch.draw(hudScoreTex, scoreX, iconY, iconSize, iconSize)
        font.draw(batch, "$score", scoreX + iconSize + gap, textY)

        // TIMER
        val mins = (timeLeft / 60).toInt()
        val secs = (timeLeft % 60).toInt()
        batch.draw(hudTimerTex, timerX, iconY, iconSize, iconSize)
        font.color = if (timeLeft <= 10f) Color.RED else Color.WHITE
        font.draw(batch, "%d:%02d".format(mins, secs), timerX + iconSize + gap, textY)

        // BOMB
        font.color = Color.WHITE
        batch.draw(hudBombTex, bombX, iconY, iconSize, iconSize)
        font.draw(batch, "x${player.bombs}", bombX + iconSize + gap, textY)

        batch.end()
    }

    override fun dispose() {
        super.dispose()
        bgStage1.dispose()
        bgStage2.dispose()
        hudHpTex.dispose()
        hudBombTex.dispose()
        hudScoreTex.dispose()
        hudTimerTex.dispose()
    }
}

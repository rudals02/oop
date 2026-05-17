package com.oop.game.neon

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld

/**
 * 네온 슈팅 게임의 메인 월드.
 *
 * 담당: 설재우
 */
class NeonWorld(
    screenWidth: Float,
    screenHeight: Float
) : GameWorld(screenWidth, screenHeight) {

    // ── 게임 상태 ──
    enum class GameState { PLAYING, GAME_OVER }
    private var state = GameState.PLAYING

    // ── 게임 객체 ──
    private val player = Player(
        x = screenWidth / 2f - 24f,
        y = 80f,
        worldWidth = screenWidth,
        worldHeight = screenHeight
    )

    // Enemy 리스트 — 충돌 처리 및 제거에 사용
    private val enemies = mutableListOf<Enemy>()

    // 점수
    var score = 0
        private set

    private val bgTexture = Texture(Gdx.files.internal("background.png"))

    init {
        // 플레이어 등록
        add(player)

        // 임시 DroneEnemy 2개 등록 (통합 테스트용)
        spawnDrone(screenWidth * 0.25f, screenHeight - 150f)
        spawnDrone(screenWidth * 0.75f, screenHeight - 200f)
    }

    private fun spawnDrone(x: Float, y: Float) {
        val drone = DroneEnemy(x, y, minX = 0f, maxX = screenWidth)
        enemies.add(drone)
        add(drone)
    }

    // ── 매 프레임 로직 ──

    override fun update(delta: Float) {
        when (state) {
            GameState.PLAYING  -> updatePlaying(delta)
            GameState.GAME_OVER -> Unit
        }
    }

    private fun updatePlaying(delta: Float) {
        // 1) 모든 등록 객체 갱신 (player, enemies)
        updateAllObjects(delta)

        // 2) 총알 ↔ 적 충돌
        val bulletsToKill  = mutableListOf<Bullet>()
        val enemiesToKill  = mutableListOf<Enemy>()

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

        // 3) 죽은 적 처리
        for (enemy in enemiesToKill) {
            score += enemy.score
            enemies.remove(enemy)
            remove(enemy)
        }

        // 4) 죽은 총알 정리 (player.bullets 에서 제거)
        player.bullets.removeAll { !it.isAlive() }

        // 5) 플레이어 ↔ 적 충돌
        for (enemy in enemies) {
            if (!enemy.isDead() && player.collidesWith(enemy)) {
                player.takeDamage()
            }
        }

        // 6) 게임 오버 판정
        if (player.isDead()) state = GameState.GAME_OVER

        // 7) isAlive=false 객체 정리
        removeDead()
    }

    // ── 그리기 ──

    override fun drawBackground(batch: SpriteBatch) {
        batch.draw(bgTexture, 0f, 0f, screenWidth, screenHeight)
    }

    override fun render(delta: Float) {
        super.render(delta)

        // 총알은 GameWorld 오브젝트 리스트에 없으므로 직접 그림
        batch.begin()
        for (bullet in player.bullets) {
            if (bullet.isAlive()) bullet.draw(batch)
        }
        batch.end()

        // HUD는 항상 맨 위에
        drawHud()

        if (state == GameState.GAME_OVER) {
            drawTextOnScreen("GAME OVER", screenWidth / 2f - 70f, screenHeight / 2f, Color.RED, 2f)
        }
    }

    private fun drawHud() {
        val topY = screenHeight - 12f

        batch.begin()
        font.data.setScale(1.2f)

        // HP — 좌측 상단
        font.color = Color.RED
        font.draw(batch, "HP : ${player.hp}", 12f, topY)

        // BOMB — 우측 상단
        font.color = Color.YELLOW
        font.draw(batch, "BOMB : ${player.bombs}", screenWidth - 130f, topY)

        // SCORE — 중앙 상단
        font.color = Color.WHITE
        font.draw(batch, "SCORE : $score", screenWidth / 2f - 50f, topY)

        batch.end()
    }

    override fun dispose() {
        super.dispose()
        bgTexture.dispose()
    }
}

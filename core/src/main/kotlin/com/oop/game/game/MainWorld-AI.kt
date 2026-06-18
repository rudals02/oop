package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameWorld
import com.oop.game.base.InputHandler

// ─────────────────────────────────────────────────
// [1] sealed class: 상태마다 데이터를 다르게 가질 수 있음
//     enum은 모든 상태가 같은 구조여야 하지만
//     sealed class는 각 상태가 자기만의 프로퍼티를 가짐
// ─────────────────────────────────────────────────
sealed class GameState {
    object StageStart : GameState()
    object Playing : GameState()
    data class StageClear(val clearedStage: Int) : GameState()   // 어떤 스테이지가 클리어됐는지 저장
    object GameOver : GameState()
}

// ─────────────────────────────────────────────────
// [2] interface + Strategy 패턴: 스테이지마다 다른 행동
//     스테이지를 추가할 때 MainWorld를 건드리지 않아도 됨
// ─────────────────────────────────────────────────
interface StageStrategy {
    val number: Int
    fun onEnter(world: MainWorldAI)
    fun update(world: MainWorldAI, delta: Float)
    fun isCleared(world: MainWorldAI): Boolean
}

private class Stage1 : StageStrategy {
    override val number = 1

    override fun onEnter(world: MainWorldAI) {
        val sw = world.width
        val sh = world.height
        world.spawnDrone(sw * 0.10f, sh - 150f)
        world.spawnDrone(sw * 0.30f, sh - 200f)
        world.spawnDrone(sw * 0.50f, sh - 150f)
        world.spawnDrone(sw * 0.70f, sh - 200f)
        world.spawnDrone(sw * 0.90f, sh - 150f)
    }

    override fun update(world: MainWorldAI, delta: Float) {}
    override fun isCleared(world: MainWorldAI) = world.enemies.isEmpty()
}

private class Stage2 : StageStrategy {
    override val number = 2
    private var spawnTimer = 0f
    private var spawnCount = 0

    // [3] companion object: 클래스 고유 상수를 companion object로 묶음
    //     Java의 static final과 같지만 Kotlin다운 방식
    companion object {
        private const val MAX_RUSH = 7
        private const val SPAWN_INTERVAL = 0.7f
    }

    override fun onEnter(world: MainWorldAI) {
        spawnTimer = 0f
        spawnCount = 0
    }

    override fun update(world: MainWorldAI, delta: Float) {
        if (spawnCount >= MAX_RUSH) return
        spawnTimer += delta
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0f
            spawnCount++
            world.spawnRush(
                x = (Math.random() * (world.width - 60f)).toFloat(),
                y = world.height - 150f - (Math.random() * 220f).toFloat()
            )
        }
    }

    override fun isCleared(world: MainWorldAI) = world.enemies.isEmpty() && spawnCount >= MAX_RUSH
}

private class Stage3 : StageStrategy {
    override val number = 3

    override fun onEnter(world: MainWorldAI) {
        val boss = BossEnemy(world.width / 2f - 45f, world.height - 140f)
        world.registerBoss(boss)
        repeat(6) {
            world.spawnDrone(
                x = (Math.random() * (world.width - 48f)).toFloat(),
                y = world.height - 180f - (Math.random() * 250f).toFloat()
            )
        }
        repeat(10) {
            world.spawnRush(
                x = (Math.random() * (world.width - 60f)).toFloat(),
                y = world.height - 180f - (Math.random() * 250f).toFloat()
            )
        }
    }

    override fun update(world: MainWorldAI, delta: Float) {}
    override fun isCleared(world: MainWorldAI) = world.enemies.isEmpty()
}

// ─────────────────────────────────────────────────
// [4] 단일 책임 원칙(SRP): HUD 렌더링을 별도 클래스로 분리
//     MainWorld가 모든 걸 알 필요 없음
// ─────────────────────────────────────────────────
private class HudRenderer(private val sw: Float, private val sh: Float) {
    private val iconSize = 28f
    private val iconY = sh - iconSize - 6f
    private val textY = sh - 12f
    private val gap = 4f
    private val sectionW = sw / 4f

    private val hpTex    = Texture(Gdx.files.internal("hp.png"))
    private val bombTex  = Texture(Gdx.files.internal("bomb_background.png"))
    private val scoreTex = Texture(Gdx.files.internal("score.png"))
    private val timerTex = Texture(Gdx.files.internal("timer.png"))

    fun draw(batch: SpriteBatch, font: BitmapFont, player: Player, score: Int, timeLeft: Float) {
        batch.begin()
        font.data.setScale(0.95f)
        font.color = Color.WHITE

        val hpX = sectionW * 0f + 6f
        batch.draw(hpTex, hpX, iconY, iconSize, iconSize)
        font.draw(batch, "x${player.hp}", hpX + iconSize + gap, textY)

        val scoreX = sectionW * 1f + 4f
        batch.draw(scoreTex, scoreX, iconY, iconSize, iconSize)
        font.draw(batch, "$score", scoreX + iconSize + gap, textY)

        val timerX = sectionW * 2f + 4f
        val mins = (timeLeft / 60).toInt()
        val secs = (timeLeft % 60).toInt()
        batch.draw(timerTex, timerX, iconY, iconSize, iconSize)
        font.color = if (timeLeft <= 10f) Color.RED else Color.WHITE
        font.draw(batch, "%d:%02d".format(mins, secs), timerX + iconSize + gap, textY)

        font.color = Color.WHITE
        val bombX = sectionW * 3f + 4f
        batch.draw(bombTex, bombX, iconY, iconSize, iconSize)
        font.draw(batch, "x${player.bombs}", bombX + iconSize + gap, textY)

        batch.end()
    }

    fun dispose() {
        hpTex.dispose()
        bombTex.dispose()
        scoreTex.dispose()
        timerTex.dispose()
    }
}

class MainWorldAI(
    screenWidth: Float,
    screenHeight: Float
) : GameWorld(screenWidth, screenHeight) {

    // [5] companion object: 전역 상수를 클래스 레벨에서 관리
    companion object {
        private const val CLEAR_DURATION   = 2.0f
        private const val START_DURATION   = 1.5f
        private const val ITEM_SPAWN_INTERVAL = 3f
        private const val INITIAL_TIME     = 150f
    }

    private var state: GameState = GameState.StageStart
    private var transitionTimer = 0f

    // Strategy 패턴: 스테이지 리스트로 관리 → 스테이지 추가/제거가 쉬움
    private val stageList: List<StageStrategy> = listOf(Stage1(), Stage2(), Stage3())
    private var stageIndex = 0
    private val currentStage get() = stageList[stageIndex]

    private val fireballs    = mutableListOf<Fireball>()
    private val effects      = mutableListOf<HitEffect>()
    private val enemyBullets = mutableListOf<EnemyBullet>()
    private val playerBullets = mutableListOf<Bullet>() //playerbullet도 여기 추가 하였습니다
    private val playerBombs = mutableListOf<BombProjectile>()
    internal var boss: BossEnemy? = null

    // [6] internal 가시성: 같은 모듈(StageStrategy 구현체)에서 접근 가능
    //     private보다 넓고 public보다 좁은 중간 단계
    internal val enemies = mutableListOf<Enemy>()
    internal val width   = screenWidth
    internal val height  = screenHeight

    private val player = Player(
        x = screenWidth / 2f - 24f,
        y = 80f,
        worldWidth = screenWidth,
        worldHeight = screenHeight
    )

    private val items = mutableListOf<Item>()
    private var itemSpawnTimer = 0f

    var score = 0
        private set

    // [7] by lazy: 처음 접근하는 순간 초기화 → 불필요한 리소스 선점 방지
    private val backgrounds by lazy {
        mapOf(
            1 to Texture(Gdx.files.internal("stage1.png")),
            2 to Texture(Gdx.files.internal("stage2.png")),
            3 to Texture(Gdx.files.internal("stage3.png"))
        )
    }

    private val hud = HudRenderer(screenWidth, screenHeight)
    private var timeLeft = INITIAL_TIME

    init {
        add(player)
        currentStage.onEnter(this)
    }

    // internal 함수: StageStrategy 구현체에서 적을 소환하기 위해 노출
    internal fun spawnDrone(x: Float, y: Float) {
        val drone = DroneEnemy(x, y, minX = 0f, maxX = screenWidth)
        enemies.add(drone); add(drone)
    }

    internal fun spawnRush(x: Float, y: Float) {
        val rush = RushEnemy(x, y, minX = 0f, maxX = screenWidth)
        enemies.add(rush); add(rush)
    }

    internal fun registerBoss(b: BossEnemy) {
        boss = b; enemies.add(b); add(b)
    }

    override fun update(delta: Float) {
        // [8] sealed class의 when: else 없이 모든 케이스를 강제로 처리
        //     새 상태 추가 시 컴파일 에러로 누락 방지
        when (state) {
            GameState.StageStart      -> updateStageStart(delta)
            GameState.Playing         -> updatePlaying(delta)
            is GameState.StageClear   -> updateStageClear(delta)
            GameState.GameOver        -> Unit
        }
    }

    private fun updateStageStart(delta: Float) {
        transitionTimer += delta
        if (transitionTimer >= START_DURATION) {
            transitionTimer = 0f
            state = GameState.Playing
        }
    }

    private fun updateStageClear(delta: Float) {
        transitionTimer += delta
        if (transitionTimer >= CLEAR_DURATION + START_DURATION) {
            transitionTimer = 0f
            advanceStage()
        }
    }

    // 스테이지 전환 책임을 하나의 함수로 집중
    private fun advanceStage() {
        stageIndex++
        if (stageIndex >= stageList.size) {
            state = GameState.GameOver
        } else {
            currentStage.onEnter(this)
            state = GameState.Playing
        }
    }

    private fun updatePlaying(delta: Float) {
        updateAllObjects(delta)
        currentStage.update(this, delta)   // 스테이지별 업데이트는 위임

        val killed = mutableSetOf<Enemy>()

        for (bullet in player.bullets) {
            for (enemy in enemies) {
                if (!enemy.isDead() && bullet.isAlive() && bullet.collidesWith(enemy)) {
                    bullet.kill()
                    enemy.takeDamage(bullet.damage)
                    if (enemy.isDead()) killed.add(enemy)
                }
            }
        }

        if (InputHandler.isKeyJustPressed(InputHandler.Z)) player.useBomb()

        for (bomb in player.bombProjectiles) {
            for (enemy in enemies) {
                if (!enemy.isDead() && bomb.isAlive() && bomb.collidesWith(enemy)) {
                    bomb.kill()
                    enemy.takeDamage(bomb.damage)
                    effects.add(HitEffect(enemy.x, enemy.y))
                    if (enemy.isDead()) killed.add(enemy)
                }
            }
        }

        player.bombProjectiles.removeAll { !it.isAlive() }

        for (enemy in killed) {
            score += enemy.score
            enemies.remove(enemy)
            remove(enemy)
            if (enemy == boss) boss = null
        }

        player.bullets.removeAll { !it.isAlive() }

        boss?.let { if (!it.isDead() && it.canShoot(delta)) fireballs.add(it.shootFireball()) }

        for (enemy in enemies) {
            if (enemy is DroneEnemy && !enemy.isDead() && enemy.canShoot(delta)) {
                enemyBullets.add(enemy.shoot())
            }
        }

        fireballs.forEach { it.update(delta) }
        enemyBullets.forEach { it.update(delta) }

        for (fireball in fireballs) {
            if (fireball.isAlive() && player.collidesWith(fireball)) {
                if (player.takeDamage()) effects.add(HitEffect(player.x, player.y))
                fireball.kill()
            }
        }

        for (enemyBullet in enemyBullets) {
            if (enemyBullet.isAlive() && player.collidesWith(enemyBullet)) {
                if (player.takeDamage()) effects.add(HitEffect(player.x, player.y))
                enemyBullet.kill()
            }
        }

        fireballs.removeAll { !it.isAlive() }
        enemyBullets.removeAll { !it.isAlive() }

        for (enemy in enemies) {
            if (!enemy.isDead() && player.collidesWith(enemy)) {
                if (player.takeDamage()) effects.add(HitEffect(player.x, player.y))
            }
        }

        itemSpawnTimer += delta
        if (itemSpawnTimer >= ITEM_SPAWN_INTERVAL) {
            itemSpawnTimer = 0f
            spawnItem()
        }

        items.forEach { it.update(delta) }
        items.filter { player.collidesWith(it) }.forEach { item ->
            when (item.type) {
                ItemType.HEAL -> player.heal()
                ItemType.BOMB -> player.addBomb()
            }
            item.collect()
        }
        items.removeAll { !it.isAlive() }

        effects.forEach { it.update(delta) }
        effects.removeAll { !it.isAlive() }

        timeLeft -= delta
        if (timeLeft <= 0f) {
            timeLeft = 0f
            state = GameState.GameOver
        }

        if (currentStage.isCleared(this)) {
            state = GameState.StageClear(currentStage.number)
            transitionTimer = 0f
        }

        if (player.isDead()) state = GameState.GameOver

        removeDead()
    }

    private fun spawnItem() {
        val x = (Math.random() * (screenWidth - 32f)).toFloat()
        val type = if (Math.random() < 0.5) ItemType.HEAL else ItemType.BOMB
        items.add(Item(x, screenHeight, type))
    }

    override fun drawBackground(batch: SpriteBatch) {
        val bg = backgrounds[currentStage.number] ?: backgrounds[1]!!
        batch.draw(bg, 0f, 0f, screenWidth, screenHeight)
    }

    override fun render(delta: Float) {
        super.render(delta)

        batch.begin()

        // [9] filter + forEach: 조건부 렌더링을 함수형으로 표현
        player.bullets.filter { it.isAlive() }.forEach { it.draw(batch) }
        player.bombProjectiles.filter { it.isAlive() }.forEach { it.draw(batch) }
        fireballs.filter { it.isAlive() }.forEach { it.draw(batch) }
        enemyBullets.filter { it.isAlive() }.forEach { it.draw(batch) }
        items.filter { it.isAlive() }.forEach { it.draw(batch) }
        effects.filter { it.isAlive() }.forEach { it.draw(batch) }

        drawEnemyHp()
        batch.end()

        hud.draw(batch, font, player, score, timeLeft)

        // sealed class의 when: 스마트 캐스트로 StageClear의 clearedStage에 직접 접근
        when (val s = state) {
            GameState.StageStart -> {
                val flash = flashAlpha()
                drawTextOnScreen("STAGE 1 START!", screenWidth / 2f - 95f, screenHeight / 2f, Color(0.3f, 1f, 0.5f, flash), 1.6f)
            }
            is GameState.StageClear -> {
                val flash = flashAlpha()
                if (transitionTimer < CLEAR_DURATION) {
                    drawTextOnScreen("STAGE ${s.clearedStage} CLEAR!", screenWidth / 2f - 95f, screenHeight / 2f, Color(1f, 0.85f, 0f, flash), 1.6f)
                } else {
                    drawTextOnScreen("STAGE ${s.clearedStage + 1} START!", screenWidth / 2f - 95f, screenHeight / 2f, Color(0.3f, 1f, 0.5f, flash), 1.6f)
                }
            }
            GameState.GameOver -> drawTextOnScreen("GAME OVER", screenWidth / 2f - 70f, screenHeight / 2f, Color.RED, 2f)
            GameState.Playing  -> Unit
        }
    }

    // 반복 계산을 함수로 이름 붙여 의미를 명확하게
    private fun flashAlpha() = (Math.sin(transitionTimer * 6.0) * 0.4 + 0.6).toFloat()

    private fun drawEnemyHp() {
        font.data.setScale(0.75f)
        font.color = Color.RED
        for (enemy in enemies) {
            if (!enemy.isDead()) {
                font.draw(batch, "${enemy.hp}/${enemy.maxHp}", enemy.x, enemy.y + enemy.height + 14f)
            }
        }
        font.color = Color.WHITE
    }

    override fun dispose() {
        super.dispose()
        backgrounds.values.forEach { it.dispose() }
        hud.dispose()
        fireballs.forEach { it.dispose() }
        effects.forEach { it.dispose() }
        items.forEach { it.dispose() }
        enemyBullets.forEach { it.dispose() }
    }
}

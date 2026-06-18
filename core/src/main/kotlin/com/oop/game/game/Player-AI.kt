package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.oop.game.base.GameObject
import com.oop.game.base.InputHandler

class PlayerAI(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
    private val onShoot: (bulletX: Float, bulletY: Float) -> Unit,
    private val onBombShoot: (bombX: Float, bombY: Float) -> Unit
) : GameObject(x, y, 32f, 32f) { //필수정보 6개 + 게임오브젝트의 자식
    /*
   Player은 총알 만드는 법을 모름 스페이스 바를 누르면 onShoot가 진동벨 울려줌
   진동벨 울리면 MainWorld가 그 신호(좌표와 방향)받아 진짜 Bullet 객체 채워 넣어줌
     */

    // --- 애니메이션 및 이미지 관련 변수 ---
    private val walkSheet = Texture(Gdx.files.internal("player_walk_sheet.png"))
    private val damagedTexture = Texture(Gdx.files.internal("player_damaged.png"))

    private val walkAnimation: Animation<TextureRegion>
    private val idleFrame: TextureRegion
    private var stateTime = 0f

    // --- 게임 플레이 관련 변수 ---
    private val speed = 300f
    val maxHp = 3
    var hp: Int = maxHp
        private set
    var bombCount: Int = 0

    //시간이 흐르면(매 프레임) 총알 쿨타임과 무적 타이머 스톱 워치 시간 감소
    private val shootCooldown = 0.15f
    private var shootTimer = 0f

    private val invincibilityDuration = 1.0f
    private var invincibilityTimer = 0f

    init {
        // 스프라이트 시트 분할 및 애니메이션 설정
        val frameWidth = 32
        val frameHeight = 32
        val tmp = TextureRegion.split(walkSheet, frameWidth, frameHeight)

        idleFrame = tmp[0][0] // 대기 상태 이미지
        val walkFrames = Array(tmp[0].size - 1) { i -> tmp[0][i + 1] } // 걷기 프레임들

        walkAnimation = Animation(0.1f, *walkFrames)
        walkAnimation.playMode = Animation.PlayMode.LOOP
    }

    /*
    Player가 태어날 때, 걷는 사진 조각들(walkFrames)을 순서대로 애니메이션 기계에 넣고 1장씩 넘김

    게임이 돌아가는 동안(매 프레임마다), 플레이어가 앞으로 이동하고 있으면 (stateTime)에 시간을 계속 누적합니다.
    (예: 0.13초, 0.25초...)

    화면을 그릴 때(draw) 애니메이션 기계에게 스톱워치를 보여줍니다.
    "나 지금 0.25초만큼 지남 무슨 사진 그려야 돼?" 그러면 기계가 알아서 맞는 currentFrame을 꺼냄
     */

    override fun update(delta: Float) {
        // 1. 타이머 관리
        if (shootTimer > 0f) shootTimer -= delta
        if (invincibilityTimer > 0f) invincibilityTimer -= delta

        // 2. 이동 처리
        if (InputHandler.isKeyPressed(InputHandler.LEFT))  x -= speed * delta
        if (InputHandler.isKeyPressed(InputHandler.RIGHT)) x += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.UP))    y += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.DOWN))  y -= speed * delta

        // 3. 월드 경계 제한
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)

        // 4. 애니메이션 시간 업데이트 (UP 키 누를 때만 재생)
        if (InputHandler.isKeyPressed(InputHandler.UP)) {
            stateTime += delta
        } else {
            stateTime = 0f
        }

        // 5. 총알/폭탄 발사 (아래에 분리된 총기 관리 함수 호출)
        handleShooting()
    }

    /**
     *  update()가 너무 길어지는 것을 막기 위해
     * 총 쏘는 로직과 폭탄 던지는 로직을 떼어내서 하나의 함수로 만듦
     */
    private fun handleShooting() {
        // Space 키: 기본 총알 발사 -> 쿨타임이 다 지났을 때, 총 쏠 수 있다
        if (InputHandler.isKeyPressed(InputHandler.SPACE) && shootTimer <= 0f) {
            val spawnX = x + (width / 2f) - 4f
            val spawnY = y + height
            onShoot(spawnX, spawnY)
            shootTimer = shootCooldown
        }

        // Z 키: 폭탄 발사 (1회씩)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && bombCount > 0) {
            bombCount--
            val spawnX = x + (width / 2f)
            val spawnY = y + height
            onBombShoot(spawnX, spawnY)
        }
    }

    override fun draw(batch: SpriteBatch) {
        // 우선순위 1: 무적(피격) 상태일 때 부상 이미지 출력
        if (invincibilityTimer > 0f) {
            batch.draw(damagedTexture, x, y, width, height)
        }
        // 우선순위 2: 앞으로 가는 중일 때 걷기 애니메이션 출력
        else if (InputHandler.isKeyPressed(InputHandler.UP)) {
            val currentFrame = walkAnimation.getKeyFrame(stateTime, true)
            batch.draw(currentFrame, x, y, width, height)
        }
        // 우선순위 3: 아무것도 안 할 때 대기 이미지 출력
        else {
            batch.draw(idleFrame, x, y, width, height)
        }
    }

    fun takeDamage(amount: Int = 1) {
        if (invincibilityTimer > 0f) return //무적 무시
        hp -= amount
        if (hp < 0) hp = 0

        invincibilityTimer = invincibilityDuration
    }

    fun heal(amount: Int = 1) { hp = (hp + amount).coerceAtMost(maxHp) }//coerceAtmost 차단

    fun addBomb(amount: Int = 1) {
        bombCount += amount
        if (bombCount > 3) bombCount = 3
    }

    override fun isAlive(): Boolean { return hp > 0 } //

    override fun dispose() { //
        walkSheet.dispose()
        damagedTexture.dispose()
    }
}
package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld
import com.oop.game.InputHandler

/**
 * ════════════════════════════════════════════════════════════
 *  게임 월드 예제 — Player vs Enemy 회피 게임 (이미지 사용).
 * ════════════════════════════════════════════════════════════
 *
 *  GameWorld 를 상속해 만든 가장 작은 플레이 가능한 예제.
 *  학생은 이 파일을 참고해서 자기만의 월드를 만들면 된다.
 *
 *  ── 조작법 ──
 *   ▸ 화살표 키  : 플레이어 이동
 *   ▸ WASD      : 카메라 이동 (월드가 화면보다 커서 탐험 가능)
 *   ▸ ESC       : 게임 오버 후 종료
 *
 *  ── 사용 이미지 (core/src/main/resources/) ──
 *   ▸ player.png  — 30x30 플레이어 스프라이트
 *   ▸ enemy.png   — 40x40 적 스프라이트
 *   ▸ tile.png    — 64x64 흰색 정사각형 (체스판 배경에 색만 입혀 사용)
 *
 *  ── 게임 상태 ──
 *   IN_PLAY   : 일반 진행 (이동·충돌 체크)
 *   GAME_OVER : 충돌 후 정지, ESC 입력 대기
 *
 *  ── 텍스트 데모 ──
 *   ▸ 좌측 상단 "HP: 3"       — 화면 좌표 (카메라 움직여도 고정)
 *   ▸ 월드 중앙 "WORLD CENTER" — 월드 좌표 (카메라와 함께 이동)
 *   두 개를 같이 두어, 두 좌표계의 차이를 눈으로 확인할 수 있게 했다.
 *
 *  ── 배경 ──
 *   tile.png(흰 사각형)를 두 가지 색으로 틴트해 체스판처럼 깐다.
 *   카메라 이동을 눈으로 보여주기 위함이다.
 *   GameWorld.drawBackground(batch) 를 override 해서 그린다.
 *
 * @param screenWidth  화면에 보이는 영역 너비
 * @param screenHeight 화면에 보이는 영역 높이
 * @param worldWidth   월드 전체 너비 (화면보다 크면 WASD 로 탐험 가능)
 * @param worldHeight  월드 전체 높이
 */
class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    /**
     * 게임의 현재 상태를 나타내는 열거형.
     *
     * Boolean 깃발(isGameOver) 대신 enum 을 쓰는 이유:
     *   ▸ 상태 가짓수가 늘어날 때 깔끔히 확장 가능 (예: PAUSED, MENU, VICTORY)
     *   ▸ when 으로 분기하면 'else' 없이 모든 상태를 다뤘는지 컴파일러가 체크해줌
     *   ▸ 코드를 읽을 때 "이 게임에 어떤 상태들이 있는가" 가 한눈에 보임
     *   (7주차에서 배우는 enum class 의 전형적 활용)
     */
    private enum class GameState {
        IN_PLAY,
        GAME_OVER
    }

    // 플레이어 — 월드 중앙 하단에서 시작.
    //   월드 크기를 함께 넘겨서, 경계 밖으로 못 나가게 한다.
    private val player = ExamplePlayer(
        x = worldWidth / 2 - 15f,   // 가로 30 의 절반을 빼서 정확히 중앙
        y = 50f,
        worldWidth = worldWidth,
        worldHeight = worldHeight
    )

    // 드론 — 원형 궤도
    private val droneEnemy = DroneEnemy(
        x = worldWidth / 2f,
        y = worldHeight - 200f
    )

    // 돌진 — 빠른 수평 왕복 + 수직 진동
    private val rushEnemy = RushEnemy(
        x = 100f,
        y = worldHeight - 350f,
        minX = 0f,
        maxX = worldWidth
    )

    // 보스 — 느린 사인파 이동
    private val bossEnemy = BossEnemy(
        x = worldWidth / 2f - 45f,
        y = worldHeight - 500f
    )

    // 현재 게임 상태 — 입력/충돌에 따라 IN_PLAY ↔ GAME_OVER 로 전환된다.
    private var state = GameState.IN_PLAY

    private val bgTexture = Texture(Gdx.files.internal("background.png"))

    // HUD 아이콘을 그리기 위한 1x1 흰색 픽셀 텍스처 (색만 입혀서 사각형으로 사용)
    private val pixel: Texture = run {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val tex = Texture(pixmap)
        pixmap.dispose()
        tex
    }

    private var hp = 3
    private var bombs = 3
    private var timer = 0f

    /**
     * 생성자 본문 — 월드에 플레이어와 적을 등록한다.
     *   이렇게 등록해야 update / draw 루프에 포함된다.
     */
    init {
        add(player)
        add(droneEnemy)
        add(rushEnemy)
        add(bossEnemy)
    }

    /**
     * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경' 은 이 안에서.
     *
     * 상태별로 해야 할 일이 완전히 다르므로 when 으로 분기한다.
     * (입력 처리가 render() 가 아닌 update() 에 있는 이유:
     *  '로직과 그리기의 분리' — render 는 매 프레임 그리는 일에만 집중하고,
     *  상태 변화·입력은 update 가 책임진다.)
     */
    override fun update(delta: Float) {
        when (state) {
            GameState.IN_PLAY -> updateInPlay(delta)
            GameState.GAME_OVER -> updateGameOver()
        }
    }

    /** IN_PLAY 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크. */
    private fun updateInPlay(delta: Float) {
        // ── 카메라 이동 (WASD) ──
        //   offsetX/Y 를 바꾸면 카메라가 월드 안에서 움직인다.
        val cameraSpeed = 200f * delta
        if (InputHandler.isKeyPressed(InputHandler.W)) offsetY += cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.S)) offsetY -= cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.A)) offsetX -= cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.D)) offsetX += cameraSpeed

        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
        offsetX = offsetX.coerceIn(0f, worldWidth - screenWidth)
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

        timer += delta

        // ── 1) 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
        updateAllObjects(delta)

        // ── 2) 상호작용 결정 — 누가 누구와 부딪혀 어떻게 되는지 ──
        //   collidesWith 는 GameObject 의 메서드 → 모든 게임 객체가 자동으로 가짐.
        //   이 예제에선 충돌 시 객체를 죽이지 않고 게임 상태만 바꾼다.
        //   (총알 게임이라면 여기서 bullet.kill(), enemy.kill() 같은 처리)
        if (player.collidesWith(droneEnemy) ||
            player.collidesWith(rushEnemy) ||
            player.collidesWith(bossEnemy)) {
            state = GameState.GAME_OVER
        }

        // ── 3) 죽은 객체 정리 ──
        //   현재 예제에선 아무 것도 안 죽으므로 영향 없지만,
        //   bullet/enemy 가 추가될 때를 대비한 표준 흐름이다.
        removeDead()
    }

    /** GAME_OVER 상태에서 매 프레임 처리 — ESC 입력만 감시한다. */
    private fun updateGameOver() {
        // ESC 키가 '막 눌린 순간' 앱 종료.
        //   isKeyJustPressed 로 한 이유: 누르고 있는 동안 매 프레임 exit 호출되지 않게.
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    /**
     * 배경 그리기 — GameWorld.drawBackground(batch) 를 override.
     *
     * 부모가 이미 batch.begin() 을 호출한 상태에서 이 함수를 부르므로,
     * 여기선 batch.draw() 호출만 하면 된다. (begin/end 를 또 부르면 안 된다)
     *
     * 카메라(offset) 에 따라 타일 위치가 바뀌어 이동감을 준다.
     *   타일 인덱스 자체는 월드 좌표 격자에서 변하지 않지만,
     *   각 타일을 그릴 때 offset 만큼 빼서 화면 좌표로 변환한다.
     *
     * 색을 입히는 방법:
     *   batch.color 를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     *   tile.png 가 흰색이라 어떤 색이든 그대로 적용된다.
     *   끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground(batch: SpriteBatch) {
        batch.draw(bgTexture, 0f, 0f, screenWidth, screenHeight)
    }

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI 를 얹는다.
     *
     * 이 함수에서는 '그리기' 만 한다. 입력 처리·상태 변경은 update() 의 책임.
     *
     * 주의: super.render(delta) 가 화면 clear + 배경 + 객체까지 그리므로,
     *       텍스트는 반드시 super 호출 **이후** 그려야 가려지지 않는다.
     */
    override fun render(delta: Float) {
        super.render(delta)

        // ── 항상 보이는 UI ──
        drawHud()

        // ── 상태별로 그리는 것이 다름 ──
        when (state) {
            GameState.IN_PLAY -> {
                // 플레이 중에는 추가로 그릴 것 없음
            }
            GameState.GAME_OVER -> drawGameOverOverlay()
        }
    }

    /** 상단 HUD — HP(빨간 사각형), BOMB(노란 사각형), 타이머(중앙) */
    private fun drawHud() {
        val iconSize = 18f
        val pad = 8f
        val topY = screenHeight - pad

        batch.projectionMatrix = camera.combined
        batch.begin()

        // HP 글자 + 빨간 사각형 3개 — 좌측 상단
        font.color = Color.RED
        font.data.setScale(1f)
        font.draw(batch, "HP", pad, topY)
        val hpLabelWidth = 22f
        batch.setColor(Color.RED)
        for (i in 0 until hp) {
            batch.draw(pixel, pad + hpLabelWidth + i * (iconSize + pad), topY - iconSize, iconSize, iconSize)
        }

        // BOMB 글자 + 노란 사각형 3개 — 우측 상단
        val bombIconsWidth = bombs * (iconSize + pad)
        val bombLabelWidth = 40f
        font.color = Color.YELLOW
        font.data.setScale(1f)
        font.draw(batch, "BOMB", screenWidth - bombIconsWidth - bombLabelWidth - pad, topY)
        batch.setColor(Color.YELLOW)
        for (i in 0 until bombs) {
            val x = screenWidth - pad - (i + 1) * (iconSize + pad)
            batch.draw(pixel, x, topY - iconSize, iconSize, iconSize)
        }

        // 타이머: 중앙 상단 텍스트
        batch.setColor(Color.WHITE)
        val minutes = (timer / 60).toInt()
        val seconds = (timer % 60).toInt()
        val timerText = "%02d:%02d".format(minutes, seconds)
        font.color = Color.WHITE
        font.data.setScale(1.2f)
        font.draw(batch, timerText, screenWidth / 2 - 20f, topY)

        batch.end()
    }

    /** 게임 오버 시 화면 중앙에 띄우는 안내 메시지. */
    private fun drawGameOverOverlay() {
        drawTextOnScreen(
            text = "Game Over!",
            x = screenWidth / 2 - 80f,
            y = screenHeight / 2,
            color = Color.WHITE,
            scale = 2f
        )
        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 40f,
            color = Color.WHITE,
            scale = 1f
        )
    }

    /** 화면이 닫힐 때 — 부모도 dispose 한 뒤 우리만의 자원도 해제. */
    override fun dispose() {
        super.dispose()
        bgTexture.dispose()
        pixel.dispose()
    }
}

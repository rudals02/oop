package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

// [1] 총알의 방향을 나타내는 열거형(Enum)
//4개의 종류 가지지만 어쩌피 1개만 쓰므로 player-ai로는 1개만 구현
enum class BulletDirectionAI {
    UP, DOWN, LEFT, RIGHT
}

/**
 * 4방향으로 날아가는 플레이어의 기본 총알 클래스
 */
class BulletAI(
    startX: Float,
    startY: Float,
    private val worldHeight: Float,
    private val worldWidth: Float,
    private val direction: BulletDirectionAI
) : GameObject(startX, startY, 12f, 12f) { // 총알 크기는 12x12

    private val texture = Texture(Gdx.files.internal("bullet.png"))

    // [2] 총알의 속도와 데미지 설정 (MainWorldAI가 이 값을 읽어갑니다)
    private val speed = 500f
    val damage = 1

    override fun update(delta: Float) {
        // [3] 자신이 부여받은 방향(direction)에 따라 스스로 이동합니다.
        when (direction) {
            BulletDirectionAI.UP -> y += speed * delta
            BulletDirectionAI.DOWN -> y -= speed * delta
            BulletDirectionAI.LEFT -> x -= speed * delta
            BulletDirectionAI.RIGHT -> x += speed * delta
        }
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    // [4] 총알이 화면(월드) 밖으로 나갔는지 스스로 판단합니다.
    override fun isAlive(): Boolean {
        // x 좌표가 0 ~ worldWidth 사이이고, y 좌표가 0 ~ worldHeight 사이일 때만 생존(true)
        val inBoundsX = x >= 0f && x <= worldWidth
        val inBoundsY = y >= 0f && y <= worldHeight

        return inBoundsX && inBoundsY
    }

    // [5] 자원 해제
    override fun dispose() {
        texture.dispose()
    }

    // (참고) 적과 부딪혀서 터질 때 MainWorld가 강제로 총알을 죽이기 위해
    // 호출할 수 있는 함수를 하나 열어둡니다.
    fun kill() {
        // 화면 밖으로 보내버려서 다음 프레임 isAlive()가 false가 되게 유도하거나,
        // 아니면 aliveState 같은 변수를 하나 더 두어 false로 만들어도 됩니다.
        // 가장 간단한 꼼수: 좌표를 아예 화면 밖으로 던지는 것입니다.
        x = -9999f
        y = -9999f
    }
}
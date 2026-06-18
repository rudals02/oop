package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

// ─────────────────────────────────────────────────
// [1] 단일 책임 원칙: 타격 이펙트는 다른 객체와 상호작용하지 않는다.
//     오직 '지정된 위치에서 일정 시간 동안 이미지를 렌더링하고 사라지는 것'만 책임집니다.
// ─────────────────────────────────────────────────
class HitEffectAI(
    x: Float,
    y: Float
) : GameObject(x, y, 32f, 32f) { // 이펙트 크기는 32x32로 고정, 적 맞을 시 생성

    private val texture = Texture(Gdx.files.internal("blast.png"))

    // ─────────────────────────────────────────────
    // [2] 캡슐화 및 생명주기 관리
    //     외부(MainWorld)에서는 이펙트가 몇 초 동안 유지되는지 알 필요가 없다.
    // ─────────────────────────────────────────────
    private val duration = 0.2f // 0.2초 동안만 화면에 표시됨
    private var stateTime = 0f
    private var alive = true// 현재 살아있음

    // ─────────────────────────────────────────────
    // [3] 자기 주도적 소멸 (Garbage Collection 유도)
    //     시간이 다 되면 스스로 죽음 상태(alive = false)로 전환하여
    //     MainWorld의 effects.removeAll { !it.isAlive() } 에 의해 청소됨
    // ─────────────────────────────────────────────
    override fun update(delta: Float) {
        if (!alive) return

        stateTime += delta
        if (stateTime >= duration) {
            alive = false//죽음
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (alive) {
            batch.draw(texture, x, y, width, height)
        }
    }

    override fun isAlive(): Boolean = alive

    override fun dispose() {
        texture.dispose()
    }
}
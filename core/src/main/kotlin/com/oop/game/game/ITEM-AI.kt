package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

// ─────────────────────────────────────────────────
// [1] enum class: 아이템 종류를 타입으로 표현
//     문자열/정수 대신 enum을 쓰면 오타로 인한 버그가 없고
//     when 사용 시 컴파일러가 누락된 케이스를 잡아줌
// ─────────────────────────────────────────────────
// ─────────────────────────────────────────────────
// [2] 상속(Inheritance): ItemAI는 GameObject를 상속
//     위치(x, y), 크기(width, height), 충돌(collidesWith) 등
//     공통 기능은 부모에서 물려받고, 아이템 고유 로직만 여기서 구현
// ─────────────────────────────────────────────────
class ItemAI(
    x: Float,
    y: Float,
    val type: ItemType   // val: 수집 후에도 종류를 알아야 하므로 공개
) : GameObject(x, y, 32f, 32f) {

    private val speed = 250f
    private var alive = true

    // ─────────────────────────────────────────────
    // [3] when 표현식(Expression): 값을 반환하는 when
    //     if-else 체인보다 간결하고, 새 ItemType 추가 시
    //     else 없는 when은 컴파일 에러로 누락을 강제로 알려줌
    // ─────────────────────────────────────────────
    private val texture: Texture = when (type) {
        ItemType.BOMB -> Texture(Gdx.files.internal("bomb.png"))
        ItemType.HEAL -> Texture(Gdx.files.internal("heal.png"))
    }

    // ─────────────────────────────────────────────
    // [4] override: 부모(GameObject)의 추상 메서드를 구체화
    //     다형성(Polymorphism) 덕분에 호출부는 Item인지 몰라도
    //     update(delta)만 호출하면 올바른 동작이 실행됨
    // ─────────────────────────────────────────────
    override fun update(delta: Float) {
        y -= speed * delta
        if (y + height < 0f) alive = false  // 화면 밖으로 나가면 자동 소멸
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    // ─────────────────────────────────────────────
    // [5] 상태 캡슐화(Encapsulation): alive 필드는 private
    //     외부에서는 isAlive()로만 상태를 읽고,
    //     collect()를 통해서만 수집 상태로 바꿀 수 있음
    //     → 외부 코드가 alive를 직접 조작하는 걸 막아 버그 예방
    // ─────────────────────────────────────────────
    override fun isAlive(): Boolean = alive

    fun collect() { alive = false }

    override fun dispose() {
        texture.dispose()
    }
}

package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

// ─────────────────────────────────────────────────
// [1] 상속(Inheritance): BombProjectile도 GameObject를 상속
//     위치/크기/충돌 기반 코드를 중복 없이 재사용하고
//     폭탄 고유 로직(폭발, 다중 타격 방지)만 이 클래스에 집중
// ─────────────────────────────────────────────────
class BombProjectileAI(
    x: Float,
    y: Float,
    private val worldHeight: Float
) : GameObject(x, y, 24f, 24f) {

    val damage = 3
    private val speed = 350f
    private var alive = true

    // ─────────────────────────────────────────────
    // [2] private set (캡슐화): isExploded는 외부에서 읽을 수 있지만
    //     변경은 이 클래스만 가능 → 상태 전이를 explode() 한 곳에서만 제어
    //     외부 코드가 bomb.isExploded = true 로 직접 바꾸는 걸 막음
    // ─────────────────────────────────────────────
    var isExploded = false; private set
    private var explosionTimer = 0f
    private val explosionEffect = 0.5f
    val explosionRadius = 150f

    // ─────────────────────────────────────────────
    // [3] 다중 타격 방지 패턴: 폭발 범위 안의 적을 한 번만 타격하기 위해
    //     이미 때린 적을 컬렉션으로 기억
    //     → 상태(hitEnemies)를 객체 안에 캡슐화해 로직을 단순하게 유지
    // ─────────────────────────────────────────────
    private val hitEnemies = mutableListOf<GameObject>()

    private val bombtexture = Texture(Gdx.files.internal("bomb.png"))
    private val explosiontexture = Texture(Gdx.files.internal("blast.png"))

    // ─────────────────────────────────────────────
    // [4] 상태 패턴(State-like): isExploded 플래그에 따라
    //     update() 내부 동작이 완전히 달라짐
    //     상태가 늘어나면 State 패턴 클래스로 분리하는 것도 고려할 수 있음
    // ─────────────────────────────────────────────
    override fun update(delta: Float) {
        if (!isExploded) {
            y += speed * delta
            if (y >= 400f) explode()
        } else {
            explosionTimer += delta
            if (explosionTimer >= explosionEffect) alive = false
        }
    }

    // ─────────────────────────────────────────────
    // [5] Guard clause (방어적 프로그래밍): 이미 폭발한 경우 즉시 return
    //     중첩 if 없이 메서드 상단에서 불가능한 케이스를 차단
    //     → 이후 코드는 "아직 폭발 안 됨"이 보장된 상태에서 동작
    // ─────────────────────────────────────────────
    fun explode() {
        if (isExploded) return
        isExploded = true

        // 폭발 시 충돌 영역을 폭발 반경으로 교체 (중심 기준 확장)
        val centerX = x + width / 2f
        val centerY = y + height / 2f
        width = explosionRadius
        height = explosionRadius
        x = centerX - width / 2f
        y = centerY - height / 2f
    }

    // ─────────────────────────────────────────────
    // [6] 메서드 오버라이드(Override): 부모의 collidesWith를 재정의
    //     super.collidesWith()로 기본 충돌 검사를 재사용하고,
    //     중복 타격 방지 로직만 추가 → 부모 로직을 복붙하지 않음
    //     (Open/Closed Principle: 부모를 수정하지 않고 확장)
    // ─────────────────────────────────────────────
    override fun collidesWith(other: GameObject): Boolean {
        if (hitEnemies.contains(other)) return false
        val isCollided = super.collidesWith(other)
        if (isCollided) hitEnemies.add(other)
        return isCollided
    }

    override fun draw(batch: SpriteBatch) {
        if (!isExploded) {
            batch.draw(bombtexture, x, y, width, height)
        } else {
            batch.draw(explosiontexture, x, y, width, height)
        }
    }

    override fun isAlive(): Boolean = alive

    // kill()은 외부에서 "강제 폭발" 요청 시 사용 — explode()로 위임
    fun kill() { if (!isExploded) explode() }

    override fun dispose() {
        bombtexture.dispose()
        explosiontexture.dispose()
        hitEnemies.clear()
    }
}

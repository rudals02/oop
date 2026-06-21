package com.oop.game.game

import com.oop.game.base.GameObject

/**
 * 모든 적의 공통 부모 클래스.
 *
 * 체력, 점수, 속도 외에 발사 쿨다운 로직도 공통으로 끌어올렸다.
 * (DroneEnemy, BossEnemy가 동일한 타이머 패턴을 각자 구현하고 있었음)
 */
abstract class Enemy(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    initialHp: Int,
    val score: Int,
    val speed: Float
) : GameObject(x, y, width, height) {

    val maxHp: Int = initialHp

    var hp: Int = initialHp
        protected set

    private var shootTimer = 0f
    protected open val shootInterval: Float = Float.MAX_VALUE // 발사 안 하는 적은 무한대로 둠

    open fun takeDamage(damage: Int) {
        hp = (hp - damage).coerceAtLeast(0)
    }

    fun isDead(): Boolean = hp <= 0

    override fun isAlive(): Boolean = !isDead()

    /** 매 프레임 호출. 쿨다운이 찼으면 true를 반환하고 타이머를 리셋한다. */
    protected fun tickShootTimer(delta: Float): Boolean {
        shootTimer += delta
        if (shootTimer >= shootInterval) {
            shootTimer = 0f
            return true
        }
        return false
    }
}
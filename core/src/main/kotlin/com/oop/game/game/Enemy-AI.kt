package com.oop.game.game

import com.oop.game.base.GameObject

/**
 * 모든 적의 공통 부모 클래스.
 *
 * 체력, 점수, 속도만 공통으로 가진다.
 * 발사 가능 여부는 Shooter 인터페이스로 분리했다.
 * (RushEnemy처럼 발사 안 하는 적이 불필요한 발사 로직을
 *  상속받지 않도록 하기 위함 — LSP 위반 방지)
 */
abstract class EnemyAI(
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

    open fun takeDamage(damage: Int) {
        hp = (hp - damage).coerceAtLeast(0)
    }

    fun isDead(): Boolean = hp <= 0

    override fun isAlive(): Boolean = !isDead()
}
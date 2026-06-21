package com.oop.game.game

import com.oop.game.base.GameObject

/**
 * 모든 적의 공통 부모 클래스.
 *
 * 적들은 공통적으로 체력, 점수, 속도를 가진다.
 * DroneEnemy, RushEnemy, BossEnemy는 이 클래스를 상속해서 만든다.
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
        hp -= damage
        if (hp < 0) hp = 0
    }

    fun isDead(): Boolean = hp <= 0

    override fun isAlive(): Boolean = !isDead()
}
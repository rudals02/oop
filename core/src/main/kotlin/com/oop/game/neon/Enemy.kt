package com.oop.game.neon

import com.oop.game.GameObject

/**
 * 모든 적의 공통 부모 클래스.
 *
 * 적들은 공통적으로 체력, 점수, 속도를 가진다.
 * DroneEnemy, RushEnemy, BossEnemy는 이 클래스를 상속해서 만든다.
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

    var hp: Int = initialHp
        protected set

    /**
     * 총알이나 폭탄에 맞았을 때 체력을 감소시킨다.
     */
    open fun takeDamage(damage: Int) {
        hp -= damage

        if (hp < 0) {
            hp = 0
        }
    }

    /**
     * 체력이 0이면 죽은 상태이다.
     */
    fun isDead(): Boolean {
        return hp <= 0
    }

    /**
     * GameWorld에서 제거 여부를 판단할 때 사용한다.
     */
    override fun isAlive(): Boolean {
        return !isDead()
    }
}
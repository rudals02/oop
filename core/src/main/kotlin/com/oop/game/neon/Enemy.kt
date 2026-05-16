package com.oop.game.neon

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * 모든 적의 공통 추상 클래스.
 * DroneEnemy, RushEnemy, BossEnemy 가 이 클래스를 상속한다.
 *
 * 담당: 이경민 (서브클래스 구현)
 * 사용: 설재우 (NeonWorld 에서 takeDamage, isDead, score 호출)
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

    open fun takeDamage(damage: Int) {
        if (hp <= 0) return
        hp -= damage
    }

    fun isDead(): Boolean = hp <= 0

    override fun isAlive(): Boolean = !isDead()

    abstract override fun update(delta: Float)
    abstract override fun draw(batch: SpriteBatch)
}

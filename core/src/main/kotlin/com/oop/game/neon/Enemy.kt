package com.oop.game.neon

import com.oop.game.GameObject

/**
 * 모든 적의 공통 부모 클래스.
 *
 * DroneEnemy, RushEnemy, BossEnemy 같은 적들은
 * 공통적으로 위치, 크기, 체력, 점수, 속도를 가진다.
 *
 * 그래서 공통 부분은 Enemy에 모아두고,
 * 실제 이동 방식만 자식 클래스에서 다르게 구현한다.
 */
abstract class Enemy(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    initialHp: Int,
    val score: Int,     // 적을 처치했을 때 얻는 점수
    val speed: Float    // 적의 이동 속도
) : GameObject(x, y, width, height) {

    // 현재 체력
    // 외부에서는 읽을 수 있지만, 변경은 Enemy 내부나 자식 클래스에서만 가능
    var hp: Int = initialHp
        protected set

    /**
     * 적이 데미지를 받았을 때 호출된다.
     * Bullet이 적과 충돌하면 NeonWorld에서 이 함수를 호출할 수 있다.
     */
    open fun takeDamage(damage: Int) {
        hp -= damage
    }

    /**
     * 체력이 0 이하이면 죽은 상태로 본다.
     */
    fun isDead(): Boolean {
        return hp <= 0
    }

    /**
     * GameWorld의 removeDead()에서 사용하는 생존 여부.
     * 죽은 적은 자동으로 제거될 수 있다.
     */
    override fun isAlive(): Boolean {
        return !isDead()
    }
}
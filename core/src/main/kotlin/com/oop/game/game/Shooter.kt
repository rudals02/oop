package com.oop.game.game

/**
 * 주기적으로 무언가를 발사할 수 있는 적이 구현하는 인터페이스.
 *
 * DroneEnemy, BossEnemy처럼 발사 패턴을 가진 적만 구현하고,
 * RushEnemy처럼 발사 기능이 없는 적은 구현하지 않는다.
 */
interface Shooter {
    val shootInterval: Float
    var shootTimer: Float

    /** 매 프레임 호출. 쿨다운이 찼으면 true를 반환하고 타이머를 리셋한다. */
    fun canShoot(delta: Float): Boolean {
        shootTimer += delta
        if (shootTimer >= shootInterval) {
            shootTimer = 0f
            return true
        }
        return false
    }
}
package com.oop.game.game

/**
 * 주기적으로 무언가를 발사할 수 있는 적이 구현하는 인터페이스.
 * DroneEnemy, BossEnemy처럼 발사 기능이 있는 적만 구현한다.
 */
interface Shooter {
    val shootInterval: Float
    var shootTimer: Float

    fun canShoot(delta: Float): Boolean {
        shootTimer += delta
        if (shootTimer >= shootInterval) {
            shootTimer = 0f
            return true
        }
        return false
    }
}
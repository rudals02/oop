package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

class BombProjectile(
    x: Float,
    y: Float,
    private val worldHeight: Float
) : GameObject(x, y, 24f, 24f) {

    val damage = 3
    private val speed = 350f
    private var alive = true

    //폭탄 상태 관리
    var isExploded = false; private set
    private var explosionTimer = 0f
    private val explosionEffect = 0.5f//폭팔 이펙트가 화면에 머무는 시간
    val explosionRadius = 150f
    private val hitEnemies = mutableListOf<GameObject>()

    private val bombtexture = Texture(Gdx.files.internal("bomb.png"))
    private val explosiontexture = Texture(Gdx.files.internal("blast.png"))

    override fun update(delta: Float) {
        if (!isExploded){
        y += speed * delta
        if (y >= 400f ) {
            explode()
        }
    }else{
        explosionTimer += delta
            if (explosionTimer >= explosionEffect){
                alive = false
            }
        }
    }

    fun explode(){
        if (isExploded) return
        isExploded = true

        val centerX = x+width/2f//폭발하는 순간 이미지 중심 기준 공격 범위 넓히기
        val centerY = y+height/2f

        width = explosionRadius
        height = explosionRadius
        x = centerX - width/2f
        y = centerY - height/2f
    }

    override fun collidesWith(other: GameObject): Boolean {
        // 이미 한 번 때린 적이면 범위 안에 있어도 충돌 안 했다하는 문제 처리
        if (hitEnemies.contains(other)) {
            return false
        }

        //  부모(GameObject)의 원래 충돌 검사를 돌려서 진짜 부딪혔는지 확인
        val isCollided = super.collidesWith(other)

        // 진짜 부딪힘 + 폭탄이 터진 상태 = 가방에 이 적을 넣어 다중 타격 막음
        if (isCollided) {
            hitEnemies.add(other)
        }

        return isCollided
    }

    override fun draw(batch: SpriteBatch) {
        if (!isExploded) {
            batch.draw(bombtexture, x, y, width, height)
        }else{
            batch.draw(explosiontexture, x, y, width, height)
        }
    }

    override fun isAlive(): Boolean = alive

    fun kill() { if (!isExploded){ explode()} }

    override fun dispose() {
        bombtexture.dispose()
        explosiontexture.dispose()
        hitEnemies.clear()
    }
}

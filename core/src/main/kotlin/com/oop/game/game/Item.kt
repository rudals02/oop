package com.oop.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.base.GameObject

enum class ItemType { HEAL, BOMB }

class Item(
    x: Float,
    y: Float,
    val type: ItemType
) : GameObject(x, y, 32f, 32f) {

    private val speed = 250f
    private var alive = true

    private val texture: Texture = when (type) {
        ItemType.BOMB -> Texture(Gdx.files.internal("bomb.png"))
        ItemType.HEAL -> Texture(Gdx.files.internal("heal.png"))
    }

    override fun update(delta: Float) {
        y -= speed * delta
        if (y + height < 0f) alive = false
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    override fun isAlive(): Boolean = alive

    fun collect() { alive = false }

    override fun dispose() {
        texture.dispose()
    }
}

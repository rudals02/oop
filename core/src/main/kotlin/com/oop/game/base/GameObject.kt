package com.oop.game.base

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle

abstract class GameObject(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {

    open fun isAlive(): Boolean = true

    abstract fun update(delta: Float)
    abstract fun draw(batch: SpriteBatch)

    fun getBounds(): Rectangle = Rectangle(x, y, width, height)

    fun collidesWith(other: GameObject): Boolean = getBounds().overlaps(other.getBounds())

    open fun dispose() {}
}

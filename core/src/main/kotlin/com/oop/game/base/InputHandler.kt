package com.oop.game.base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

// Gdx.input.isKeyPressed(Input.Keys.XXX) 를 짧게 감싼 싱글톤
object InputHandler {

    fun isKeyPressed(key: Int): Boolean = Gdx.input.isKeyPressed(key)
    fun isKeyJustPressed(key: Int): Boolean = Gdx.input.isKeyJustPressed(key)

    val LEFT   = Input.Keys.LEFT
    val RIGHT  = Input.Keys.RIGHT
    val UP     = Input.Keys.UP
    val DOWN   = Input.Keys.DOWN
    val SPACE  = Input.Keys.SPACE
    val ESCAPE = Input.Keys.ESCAPE
    val W      = Input.Keys.W
    val A      = Input.Keys.A
    val S      = Input.Keys.S
    val D      = Input.Keys.D
    val Z      = Input.Keys.Z
}

package com.example.circle

import android.graphics.RectF
import kotlin.math.sqrt
import kotlin.math.pow

data class Circle(var x: Float, var y: Float, val radius: Float, val color: Int) {

    // Проверка, находится ли точка внутри круга
    fun isTouched(touchX: Float, touchY: Float): Boolean {
        val distance = sqrt((x - touchX).toDouble().pow(2.0) + (y - touchY).toDouble().pow(2.0))
        return distance <= radius
    }

    // Обновление позиции круга при перетаскивании
    fun updatePosition(touchX: Float, touchY: Float) {
        x = touchX
        y = touchY
    }
}

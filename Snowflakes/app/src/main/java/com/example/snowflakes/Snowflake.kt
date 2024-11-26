package com.example.snowflakes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    val velocity: Float,
    val radius: Float,
    var color: Int,
    var drift: Float
)

class SnowFlakes @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint()
    private lateinit var snow: Array<Snowflake>
    private var widthScreen = 0
    private var heightScreen = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#020652"))

        for (s in snow) {
            paint.color = s.color
            canvas.drawCircle(s.x, s.y, s.radius, paint)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthScreen = w
        heightScreen = h

        val random = Random(0)
        snow = Array(150) {
            val color = if (random.nextBoolean()) {
                Color.WHITE
            } else {
                Color.rgb(155, 232, 240)
            }

            Snowflake(
                x = random.nextFloat() * widthScreen,
                y = random.nextFloat() * heightScreen,
                velocity = 5 + random.nextFloat() * 5,
                radius = 10 + random.nextFloat() * 20,
                color = if (color == Color.RED) Color.parseColor("#A5D5DA") else color,
                drift = random.nextFloat() * 2 * Math.PI.toFloat()
            )
        }
        SnowTask().execute()
    }

    private fun moveSnowFlakes() {
        var allBelowScreen = true

        for (s in snow) {
            s.y += s.velocity
            s.x += sin(s.drift) * 5
            s.drift += 0.05f

            if (s.y <= heightScreen + s.radius) {
                allBelowScreen = false
            }

            if (s.x > widthScreen || s.x < 0) {
                s.x = if (s.x > widthScreen) widthScreen.toFloat() else 0f
            }
        }

        if (allBelowScreen) {
            resetSnowflakes()
        }
        postInvalidate()
    }

    private fun resetSnowflakes() {
        val random = Random(0)
        for (s in snow) {
            s.y = -random.nextFloat() * heightScreen
            s.x = random.nextFloat() * widthScreen
        }
    }

    inner class SnowTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            while (true) {
                moveSnowFlakes()
                Thread.sleep(16) // 60 FPS
            }
        }
    }
}

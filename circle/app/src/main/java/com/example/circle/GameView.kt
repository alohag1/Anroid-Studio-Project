package com.example.circle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.random.Random

class GameView(ctx: Context) : View(ctx) {
    var h = 1000
    var w = 1000
    private val paint = Paint() // краска, цвет и стиль отрисовки
    private var circles: MutableList<Circle> = mutableListOf()
    private var isDragging = false
    private var draggedCircle: Circle? = null

    // Прямоугольник (луза) с увеличенными размерами и по центру экрана
    private val rectangleWidth = 600
    private val rectangleHeight = 300
    private val rectangleLeft = (w - rectangleWidth) / 2
    private val rectangleTop = (h - rectangleHeight) / 2
    private val rectangle = Rect(rectangleLeft, rectangleTop, rectangleLeft + rectangleWidth, rectangleTop + rectangleHeight)

    private var remainingCircles = 5 // Число оставшихся кружков
    private var currentRectangleColor: Int = Color.GRAY // Начальный цвет прямоугольника

    init {
        // Генерация 5 случайных кружков
        generateCircles()

        // Выбор случайного цвета для прямоугольника из цветов кружков
        if (circles.isNotEmpty()) {
            currentRectangleColor = circles.random().color
        }
    }

    // Генерация случайных кружков
    private fun generateCircles() {
        // Очищаем список кружков
        circles.clear()

        for (i in 0 until 5) {
            val radius = 80f
            // Генерация случайных координат, чтобы круги не попадали внутрь прямоугольника
            var x: Float
            var y: Float
            do {
                x = (Random.nextFloat() * (w - radius * 2) + radius)
                y = (Random.nextFloat() * (h - radius * 2) + radius)
            } while (rectangle.contains(x.toInt(), y.toInt())) // Перегенерировать, если попали внутрь прямоугольника

            val color = Color.rgb((Random.nextFloat() * 255).toInt(), (Random.nextFloat() * 255).toInt(), (Random.nextFloat() * 255).toInt())
            circles.add(Circle(x, y, radius, color))
        }
    }

    // Сброс состояния игры
    private fun resetGame() {
        // Генерация новых кружков
        generateCircles()

        // Выбор случайного цвета для прямоугольника
        if (circles.isNotEmpty()) {
            currentRectangleColor = circles.random().color
        }

        // Сброс числа оставшихся кружков
        remainingCircles = circles.size
        invalidate() // Перерисовываем экран
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = bottom - top
        w = right - left

        // Перемещаем прямоугольник в центр экрана
        val rectangleLeft = (w - rectangleWidth) / 2
        val rectangleTop = (h - rectangleHeight) / 2
        rectangle.set(rectangleLeft, rectangleTop, rectangleLeft + rectangleWidth, rectangleTop + rectangleHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Отрисовка фона (белый цвет)
        paint.color = Color.WHITE
        canvas.drawColor(paint.color)

        // Отрисовка прямоугольника (лузы) с текущим цветом
        paint.color = currentRectangleColor
        canvas.drawRect(rectangle, paint)

        // Отрисовка кружков
        for (circle in circles) {
            paint.color = circle.color
            canvas.drawCircle(circle.x, circle.y, circle.radius, paint)
        }

        // Если все кружки закончились, выводим сообщение и перезапускаем игру
        if (remainingCircles == 0) {
            Toast.makeText(context, "Игра завершена!", Toast.LENGTH_SHORT).show()
            resetGame() // Перезапуск игры
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val touchX = it.x
            val touchY = it.y

            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Определяем, какой кружок был выбран для перетаскивания
                    draggedCircle = circles.find { circle -> circle.isTouched(touchX, touchY) }
                    isDragging = draggedCircle != null
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging && draggedCircle != null) {
                        // Обновляем позицию выбранного круга
                        draggedCircle?.updatePosition(touchX, touchY)
                        invalidate() // Перерисовываем экран
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging && draggedCircle != null) {
                        // Проверяем, находится ли кружок в прямоугольнике (лузе)
                        if (rectangle.contains(touchX.toInt(), touchY.toInt())) {
                            // Если цвет круга совпадает с цветом прямоугольника
                            if (draggedCircle!!.color == currentRectangleColor) {
                                // Если да, меняем цвет прямоугольника на цвет перетащенного кружка
                                currentRectangleColor = draggedCircle!!.color
                                invalidate() // Перерисовываем экран

                                // Удаляем кружок и уменьшаем счетчик оставшихся
                                circles.remove(draggedCircle)
                                remainingCircles--

                                // Если остались кружки, выбираем случайный цвет для прямоугольника из оставшихся
                                if (remainingCircles > 0) {
                                    currentRectangleColor = circles.random().color
                                }
                            } else {
                                // Если цвет не совпадает, не удаляем кружок, просто перерисовываем экран
                                invalidate()
                            }

                            // Если все кружки исчезли, выводим сообщение о победе и перезапускаем игру
                            if (remainingCircles == 0) {
                                Toast.makeText(context, "Игра завершена!", Toast.LENGTH_SHORT).show()
                                resetGame() // Перезапуск игры
                            }
                        }
                        isDragging = false
                    }
                }
                else -> {}
            }
        }
        return true
    }
}

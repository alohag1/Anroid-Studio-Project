package com.example.memorina

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val cardImages = arrayListOf(
        R.drawable.icons8_chest, R.drawable.icons8_chest,
        R.drawable.icons8_bomb, R.drawable.icons8_bomb,
        R.drawable.icons8_joker, R.drawable.icons8_joker,
        R.drawable.icons8_scroll, R.drawable.icons8_scroll,
        R.drawable.icons8_staff, R.drawable.icons8_staff,
        R.drawable.icons8_helmet, R.drawable.icons8_helmet,
        R.drawable.icons8_poison, R.drawable.icons8_poison,
        R.drawable.icons8_sword, R.drawable.icons8_sword
    )

    private val catViews = ArrayList<ImageView>()
    private var openCards = ArrayList<ImageView>()
    private var isProcessing = false
    private var pairsFound = 0
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(16, 16, 16, 16)
        layout.setBackgroundColor(0xFFF1DCC9.toInt())  // Устанавливаем фон

        val params = LinearLayout.LayoutParams(200, 200)
        params.weight = 1.toFloat()

        // Перемешиваем карты
        cardImages.shuffle()

        // Создаем ImageView для каждой карты
        for (i in 1..16) {
            val imageView = ImageView(applicationContext).apply {
                setImageResource(R.drawable.back)
                layoutParams = params
                tag = cardImages[i - 1]
                setOnClickListener(cardClickListener)
                setPadding(8, 8, 8, 8)
            }
            catViews.add(imageView)
        }

        // Создаем строки карт
        val rows = Array(4) { LinearLayout(applicationContext) }
        var count = 0
        for (view in catViews) {
            val row: Int = count / 4
            rows[row].addView(view)
            count++
        }
        for (row in rows) {
            layout.addView(row)
        }

        // Добавляем кнопку для перезапуска игры с отступом
        val restartButton = Button(applicationContext).apply {
            text = "Перезапустить игру"
            setOnClickListener { restartGame() }

            // Добавляем тень для кнопки
            setShadowLayer(8f, 4f, 4f, 0x80000000.toInt())  // (radius, dx, dy, color)
        }


        // Создаем LayoutParams для кнопки, с marginTop 40px
        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 40  // Устанавливаем отступ сверху
        }

        restartButton.layoutParams = buttonParams  // Добавляем кнопку в layout

        layout.addView(restartButton)  // Добавляем кнопку в layout

        setContentView(layout)  // Устанавливаем layout как основной вид
    }

    // Обработчик нажатия на карту
    private val cardClickListener = View.OnClickListener { view ->
        if (isProcessing) return@OnClickListener // Игнорируем нажатия, если идет обработка

        val imageView = view as ImageView

        // Игнорируем нажатие на уже открытые карты
        if (openCards.contains(imageView)) return@OnClickListener

        // Переворачиваем карту
        imageView.setImageResource(imageView.tag as Int)
        openCards.add(imageView)

        // Если открыты две карты, начинаем проверку
        if (openCards.size == 2) {
            isProcessing = true // Блокируем дальнейшие нажатия
            GlobalScope.launch(Dispatchers.Main) {
                delay(500) // Ждем перед проверкой
                checkCards()
                isProcessing = false // Разблокируем после проверки
            }
        }
    }

    // Проверка на совпадение карт
    private suspend fun checkCards() {
        val firstCard = openCards[0]
        val secondCard = openCards[1]

        if (firstCard.tag == secondCard.tag) {
            // Если карты совпали, делаем их некликабельными
            firstCard.isClickable = false
            secondCard.isClickable = false
            pairsFound++ // Увеличиваем счетчик найденных пар
        } else {
            // Если карты не совпали, переворачиваем их обратно
            firstCard.setImageResource(R.drawable.back)
            secondCard.setImageResource(R.drawable.back)
        }
        openCards.clear()

        // Проверяем, все ли пары найдены
        if (pairsFound == cardImages.size / 2) {
            // Выводим сообщение о победе через кастомный Toast
            showCustomToast("Поздравляю, вы победили!!!")
        }
    }

    // Перезапуск игры
    private fun restartGame() {
        // Сбросить все карты и счетчики
        pairsFound = 0
        openCards.clear()

        // Перемешиваем карты для новой игры
        cardImages.shuffle()

        // Обновляем отображение карт
        catViews.forEachIndexed { index, imageView ->
            imageView.setImageResource(R.drawable.back)
            imageView.isClickable = true
            imageView.tag = cardImages[index] // Обновляем тег для каждой карты
        }
    }

    // Кастомный Toast для сообщения о победе
    private fun showCustomToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)

        // Создаем TextView для кастомного Toast
        val textView = TextView(applicationContext).apply {
            text = message
            textSize = 23f  // Устанавливаем размер текста 28
            setTextColor(0xFF568A5A.toInt())  // Цвет текста #568A5A
            gravity = Gravity.CENTER
            isSingleLine = true  // Устанавливаем, чтобы сообщение было в одну строку
            ellipsize = android.text.TextUtils.TruncateAt.END // Если текст длинный, обрезаем
        }

        toast.view = textView  // Устанавливаем кастомный TextView для Toast

        // Устанавливаем расположение Toast чуть ниже (отступ 150 пикселей от верха)
        toast.setGravity(Gravity.TOP, 0, 620)  // Установим отступ 300 пикселей от верхней части экрана

        toast.show()  // Показываем Toast
    }
}

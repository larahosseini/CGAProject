package com.example.maexchen

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.maexchen.R.*
import java.util.Random

class MainActivity : AppCompatActivity() {
    private var imageViewDice: ImageView? = null
    lateinit var image_view_dice2: ImageView
    private val rng = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        imageViewDice = findViewById(id.image_view_dice)
        imageViewDice?.setOnClickListener(View.OnClickListener { rollDice() })
    }

    private fun rollDice() {
        val randomNumber = rng.nextInt(6) + 1
        when (randomNumber) {
            1 -> {
                imageViewDice!!.setImageResource(drawable.dice1)
                imageViewDice!!.setImageResource(drawable.dice2)
                imageViewDice!!.setImageResource(drawable.dice3)
                imageViewDice!!.setImageResource(drawable.dice4)
                imageViewDice!!.setImageResource(drawable.dice5)
                imageViewDice!!.setImageResource(drawable.dice6)
            }
            2 -> {
                imageViewDice!!.setImageResource(drawable.dice2)
                imageViewDice!!.setImageResource(drawable.dice3)
                imageViewDice!!.setImageResource(drawable.dice4)
                imageViewDice!!.setImageResource(drawable.dice5)
                imageViewDice!!.setImageResource(drawable.dice6)
            }
            3 -> {
                imageViewDice!!.setImageResource(drawable.dice3)
                imageViewDice!!.setImageResource(drawable.dice4)
                imageViewDice!!.setImageResource(drawable.dice5)
                imageViewDice!!.setImageResource(drawable.dice6)
            }
            4 -> {
                imageViewDice!!.setImageResource(drawable.dice4)
                imageViewDice!!.setImageResource(drawable.dice5)
                imageViewDice!!.setImageResource(drawable.dice6)
            }
            5 -> {
                imageViewDice!!.setImageResource(drawable.dice5)
                imageViewDice!!.setImageResource(drawable.dice6)
            }
            6 -> imageViewDice!!.setImageResource(drawable.dice6)
        }

    }
}
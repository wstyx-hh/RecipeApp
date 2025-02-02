
package com.example.recipeappfirebase

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Apply fade animation

        val recipeName = findViewById<TextView>(R.id.recipeDetailName)
        val recipeIngredients = findViewById<TextView>(R.id.recipeDetailIngredients)
        val recipeInstructions = findViewById<TextView>(R.id.recipeDetailInstructions)

        val name = intent.getStringExtra("recipe_name")
        val ingredients = intent.getStringExtra("recipe_ingredients")
        val instructions = intent.getStringExtra("recipe_instructions")

        recipeName.text = name
        recipeIngredients.text = ingredients
        recipeInstructions.text = instructions
    }


}

package com.example.recipeappfirebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var ingredientsInput: EditText
    private lateinit var instructionsInput: EditText
    private lateinit var saveButton: Button
    private val database = FirebaseDatabase.getInstance()
    private val recipeRef = database.getReference("recipes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        nameInput = findViewById(R.id.recipeNameInput)
        ingredientsInput = findViewById(R.id.recipeIngredientsInput)
        instructionsInput = findViewById(R.id.recipeInstructionsInput)
        saveButton = findViewById(R.id.saveRecipeButton)

        saveButton.setOnClickListener {
            addRecipe()
        }
    }

    private fun addRecipe() {
        val name = nameInput.text.toString()
        val ingredients = ingredientsInput.text.toString()
        val instructions = instructionsInput.text.toString()

        if (name.isNotEmpty() && ingredients.isNotEmpty() && instructions.isNotEmpty()) {
            val recipeId = recipeRef.push().key ?: return
            val recipe = Recipe(name, ingredients, instructions)
            recipeRef.child(recipeId).setValue(recipe).addOnSuccessListener {
                finish()
            }
        }
    }
}

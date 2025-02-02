
package com.example.recipeappfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(private val onRecipeClick: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes: List<Recipe> = emptyList()

    fun submitList(newList: List<Recipe>) {
        recipes = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener { onRecipeClick(recipe) }

        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(500).start()
    }


    override fun getItemCount() = recipes.size

    fun getRecipeAt(position: Int): Recipe {
        return recipes[position]
    }

    fun removeAt(position: Int) {
        val mutableList = recipes.toMutableList()
        mutableList.removeAt(position)
        submitList(mutableList)
    }


    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        private val recipeIngredients: TextView = itemView.findViewById(R.id.recipeIngredients)

        fun bind(recipe: Recipe) {
            recipeName.text = recipe.name
            recipeIngredients.text = recipe.ingredients.replace(",", "\n") // Разделяем ингредиенты по строкам
        }
    }
}
package com.example.recipeappfirebase

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var addRecipeButton: FloatingActionButton
    private lateinit var recipeAdapter: RecipeAdapter
    private val database = FirebaseDatabase.getInstance()
    private val recipeRef = database.getReference("recipes")
    private lateinit var deleteIcon: Drawable
    private lateinit var background: ColorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        addRecipeButton = findViewById(R.id.addRecipeButton)

        recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeAdapter = RecipeAdapter { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_name", recipe.name)
                putExtra("recipe_ingredients", recipe.ingredients)
                putExtra("recipe_instructions", recipe.instructions)
            }
            startActivity(intent)
        }

        recipeRecyclerView.adapter = recipeAdapter

        addRecipeButton.setOnClickListener {
            startActivity(Intent(this, AddRecipeActivity::class.java))
        }

        fetchRecipes()

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
        background = ColorDrawable(Color.RED)


        setupSwipeToDelete()
    }


    private fun fetchRecipes() {
        recipeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = snapshot.children.mapNotNull { it.getValue(Recipe::class.java) }
                recipeAdapter.submitList(recipes)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipe = recipeAdapter.getRecipeAt(position)

                // Remove from Firebase
                recipeRef.orderByChild("name").equalTo(recipe.name).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            child.ref.removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

                // Remove from Adapter
                recipeAdapter.removeAt(position)
            }

            override fun onChildDraw(
                c: android.graphics.Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val backgroundCornerOffset = 20 // Offset for rounded edges

                if (dX > 0) { // Swiping right
                    background.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                    )
                } else if (dX < 0) { // Swiping left
                    background.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom
                    )
                } else {
                    background.setBounds(0, 0, 0, 0)
                }

                background.draw(c)

                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight

                if (dX > 0) { // Swiping right
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = iconLeft + deleteIcon.intrinsicWidth
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                } else if (dX < 0) { // Swiping left
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = iconRight - deleteIcon.intrinsicWidth
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                deleteIcon.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(recipeRecyclerView)
    }
}
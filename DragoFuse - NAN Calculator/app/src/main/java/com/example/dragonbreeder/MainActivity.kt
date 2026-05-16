package com.example.dragonbreeder

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.dragofuse.BreedingLogic


/**
 * MainActivity: The main hub of DragoFuse!
 * 
 * This is where players pick their dragon eggs and see what kind of 
 * amazing hybrids they can create. We handle all the button clicks, 
 * selection highlights, and the final "fusion" logic here.
 */
class MainActivity : AppCompatActivity() {

    // A list to keep track of the two eggs currently selected by the user.
    private val selectedEggs = mutableListOf<String>()
    
    // We store our egg buttons in a map so we can easily loop through them or find them by name.
    private val buttons = mutableMapOf<String, Button>()
    
    // We save the original button color so we can reset it when an egg is un-tapped.
    private var defaultTint: ColorStateList? = null
    
    // This helper class contains the "recipes" for our dragon hybrids.
    private val logic = BreedingLogic()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Grabbing all our UI pieces from the layout file.
        val txtResult = findViewById<TextView>(R.id.txtResultName)
        val imgResult = findViewById<ImageView>(R.id.imgResult)
        val btnCombine = findViewById<Button>(R.id.btnCombine)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnInfo = findViewById<ImageButton>(R.id.btnInfo)

        // Mapping our button IDs to friendly egg names.
        val eggTypes = mapOf(
            R.id.btnFire to "Fire", R.id.btnIce to "Ice",
            R.id.btnNature to "Nature", R.id.btnElectric to "Electric",
            R.id.btnVoid to "Void"
        )

        // Setup each egg button: store it, save its color, and listen for clicks.
        for ((id, name) in eggTypes) {
            val btn = findViewById<Button>(id)
            
            // Capture the default theme color the first time we see it.
            if (defaultTint == null) defaultTint = btn.backgroundTintList
            
            buttons[name] = btn
            btn.setOnClickListener { handleEggSelection(name, btn) }
        }

        // The "Combine" button: check if we have 2 eggs, then reveal the result!
        btnCombine.setOnClickListener {
            if (selectedEggs.size == 2) {
                // Ask our logic class what these two make.
                val result = logic.combine(selectedEggs[0], selectedEggs[1])
                txtResult.text = result.name
                imgResult.setImageResource(result.imageRes)
            } else {
                // If they haven't picked exactly 2, give them a little nudge.
                Toast.makeText(this, "Select exactly 2 eggs!", Toast.LENGTH_SHORT).show()
            }
        }

        // The reset button: wipe everything and start fresh.
        btnReset.setOnClickListener { resetSelection() }

        // The info button: pop up a help dialog.
        btnInfo.setOnClickListener {
            InfoDialogFragment().show(supportFragmentManager, "info")
        }
    }

    /**
     * handleEggSelection: Manages the choosing and un-choosing of eggs.
     * We limit the selection to 2 eggs at a time and use colors to show what's selected.
     */
    private fun handleEggSelection(name: String, button: Button) {
        if (selectedEggs.contains(name)) {
            // If it's already selected, clicking it again "un-selects" it.
            selectedEggs.remove(name)
            button.backgroundTintList = defaultTint
        } else if (selectedEggs.size < 2) {
            // If we have room, add it to our list and highlight it teal.
            selectedEggs.add(name)
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#008080"))
        }
    }

    /**
     * resetSelection: Clears all current choices and returns the UI to its original state.
     */
    private fun resetSelection() {
        selectedEggs.clear()
        // Reset all button colors to their original state.
        buttons.values.forEach { it.backgroundTintList = defaultTint }
        
        // Reset the result text and image to placeholders.
        findViewById<TextView>(R.id.txtResultName).text = "Select Two Eggs"
        findViewById<ImageView>(R.id.imgResult).setImageResource(android.R.drawable.ic_menu_help)
    }
}

/**
 * InfoDialogFragment: A simple pop-up box that shows the "How to Play" info.
 */
class InfoDialogFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // We use our custom modal layout for this dialog.
        val view = inflater.inflate(R.layout.custom_modal, container, false)
        
        // Find the close button inside the modal and make it dismiss the dialog.
        view.findViewById<Button>(R.id.btnCloseModal).setOnClickListener { dismiss() }
        
        return view
    }
}

package com.example.dragofuse

import com.example.dragonbreeder.R

/**
 * DragonResult: A simple container for the dragon's name and its image.
 */
data class DragonResult(val name: String, val imageRes: Int)

/**
 * BreedingLogic: The "recipe book" for DragoFuse.
 * This class handles all the logic of which egg combinations lead to which dragons.
 */
class BreedingLogic {

    /**
     * hybridRecipes: This is our master list of all possible dragon combinations.
     * We use a Map where the 'key' is a sorted list of the two parent elements.
     * Sorting the keys ensures that (Fire + Ice) is the same as (Ice + Fire).
     */
    private val hybridRecipes = mapOf(
        listOf("Electric", "Fire") to DragonResult("Plasma Dragon", R.drawable.dragon_plasma),
        listOf("Electric", "Ice") to DragonResult("Stormpeak Dragon", R.drawable.dragon_stormpeak),
        listOf("Electric", "Nature") to DragonResult("Zenith Dragon", R.drawable.dragon_zenith),
        listOf("Electric", "Void") to DragonResult("Stormbringer", R.drawable.dragon_stormbringer),
        listOf("Fire", "Ice") to DragonResult("Frostburn Dragon", R.drawable.dragon_frostburn),
        listOf("Fire", "Nature") to DragonResult("Magma Leaf Dragon", R.drawable.dragon_magma_leaf),
        listOf("Fire", "Void") to DragonResult("Eclipse Dragon", R.drawable.dragon_eclipse),
        listOf("Ice", "Nature") to DragonResult("Evergreen Dragon", R.drawable.dragon_evergreen),
        listOf("Ice", "Void") to DragonResult("Abyssal Frost Dragon", R.drawable.dragon_abyssal_frost),
        listOf("Nature", "Void") to DragonResult("Ancient Root Dragon", R.drawable.dragon_ancient_root)
    )

    /**
     * combine: Takes two parent elements and returns the resulting dragon.
     * 
     * @param parent1 The first element selected.
     * @param parent2 The second element selected.
     * @return The offspring DragonResult.
     */
    fun combine(parent1: String, parent2: String): DragonResult {
        // We sort the parents alphabetically so the order they were clicked doesn't matter.
        val key = listOf(parent1, parent2).sorted()

        // We look up the recipe in our map. 
        // Note: This !! is safe because our UI only allows picking from the elements above.
        return hybridRecipes[key]!!
    }
}
package ie.setu.services

interface CalorieCalculator {
    fun caloriesBurned(durationMins: Int, intensityFactor: Double): Int
}

class ActivityService(private val calorieCalculator: CalorieCalculator) {
    fun calculateTotalCalories(durationMins: Int): Int {
        return calorieCalculator.caloriesBurned(durationMins, 1.2)
    }
}
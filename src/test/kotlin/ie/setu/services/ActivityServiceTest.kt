package ie.setu.services

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.junit.jupiter.api.Assertions.assertEquals

class ActivityServiceTest {

    @Test
    fun `calculateTotalCalories uses calorie calculator dependency`() {
        val mockCalculator = mock(CalorieCalculator::class.java)
        val sut = ActivityService(mockCalculator)

        // Fake the dependency’s behaviour
        `when`(mockCalculator
            .caloriesBurned(30, 1.2))
            .thenReturn(300)

        val result = sut.calculateTotalCalories(30)

        assertEquals(300, result)
        verify(mockCalculator).caloriesBurned(30, 1.2)
    }
}

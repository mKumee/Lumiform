package com.app.maincontent.usecases

import com.app.core.data.preferences.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleResponseSelectionUseCase @Inject constructor(
    private val userPreferences: PreferencesRepository
) {
    suspend operator fun invoke(questionId: Int, responseId: Int, allowMultipleSelection: Boolean) {
        val current = userPreferences.selectedResponses.first()
        val currentForQuestion = current[questionId].orEmpty()
        val updatedForQuestion = if (allowMultipleSelection) {
            if (responseId in currentForQuestion) currentForQuestion - responseId else currentForQuestion + responseId
        } else {
            if (responseId in currentForQuestion) emptySet() else setOf(responseId)
        }
        userPreferences.setSelectedResponses(current + (questionId to updatedForQuestion))
    }
}

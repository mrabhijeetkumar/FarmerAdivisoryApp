package com.example.farmeradvisoryapp.utils

object ValidationUtils {
    fun isValidQuestion(text: String): Boolean {
        return text.isNotBlank() && text.length in 3..500
    }

    fun getValidationError(text: String, minLength: Int = 3, maxLength: Int = 500): String? {
        return when {
            text.isBlank() -> "This field cannot be empty"
            text.length < minLength -> "Minimum $minLength characters required"
            text.length > maxLength -> "Maximum $maxLength characters allowed"
            else -> null
        }
    }
}

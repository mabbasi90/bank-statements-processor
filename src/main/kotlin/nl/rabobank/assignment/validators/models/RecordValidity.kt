package nl.rabobank.assignment.validators.models

/**
 * Contains the reason why a record is invalid and can check the validity of record through it's method
 */
data class RecordValidity(val reason: String?) {
    fun isValid(): Boolean = reason.isNullOrBlank()
}

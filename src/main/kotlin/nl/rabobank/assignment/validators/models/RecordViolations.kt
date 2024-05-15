package nl.rabobank.assignment.validators.models

import nl.rabobank.assignment.models.MT940Record

/**
 * Contains the reasons why a record is invalid. If there's no violations, the record is valid.
 */
data class RecordViolations(val record: MT940Record) {
    private val violations = mutableListOf<String>()

    fun addViolation(violation: String) {
        violations.add(violation)
    }

    fun getAllViolations() = violations.toList()

    fun hasViolations() = violations.isNotEmpty()

}

package nl.rabobank.assignment.validators

import nl.rabobank.assignment.models.MT940Record
import nl.rabobank.assignment.validators.models.RecordValidity
import org.springframework.stereotype.Component

const val REFERENCE_UNIQUENESS_VIOLATION = "Reference number is not unique"

/**
 * A stateful validator which checks the uniqueness of records reference numbers
 */
@Component
class UniqueReferenceValidator : RecordValidator {

    override fun validate(record: MT940Record): RecordValidity =
        RecordValidity(if (!isValid(record)) REFERENCE_UNIQUENESS_VIOLATION else null)

    private val referencesSet = mutableSetOf<Long>()

    private fun isValid(record: MT940Record): Boolean = referencesSet.add(record.reference)

}
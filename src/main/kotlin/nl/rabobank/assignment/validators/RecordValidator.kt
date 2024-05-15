package nl.rabobank.assignment.validators

import nl.rabobank.assignment.models.MT940Record
import nl.rabobank.assignment.validators.models.RecordValidity

/**
 * Interface to work with MT940 record validators
 */
fun interface RecordValidator {
    /**
     * @param record
     * @return a {@link nl.rabobank.assignment.validators.models.RecordValidity} containing the validity and the reason
     * for not validating the record
     */
    fun validate(record: MT940Record): RecordValidity
}
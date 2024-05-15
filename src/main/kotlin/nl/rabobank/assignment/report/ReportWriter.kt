package nl.rabobank.assignment.report

import nl.rabobank.assignment.validators.models.RecordViolations

/**
 * The interface for writing generated report of MT940 records and their violations
 */
fun interface ReportWriter {
    fun write(invalidRecords: List<RecordViolations>)
}
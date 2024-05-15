package nl.rabobank.assignment.report

import nl.rabobank.assignment.validators.models.RecordViolations
import org.springframework.stereotype.Component

/**
 * Writes the records and their violations into the standard output
 */
@Component
class ConsoleReportWriter : ReportWriter {
    override fun write(invalidRecords: List<RecordViolations>) {
        invalidRecords.forEach {
            println("Invalid record: ${it.record.reference} with description \"${it.record.description}\" and violations:")
            it.getAllViolations().forEach { violation ->
                println(violation)
            }
        }
    }
}
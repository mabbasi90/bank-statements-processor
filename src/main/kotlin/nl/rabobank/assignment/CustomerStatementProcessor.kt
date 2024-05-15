package nl.rabobank.assignment

import nl.rabobank.assignment.models.MT940Record
import nl.rabobank.assignment.readers.DirectoryReader
import nl.rabobank.assignment.report.ReportWriter
import nl.rabobank.assignment.validators.RecordValidator
import nl.rabobank.assignment.validators.models.RecordViolations
import org.springframework.stereotype.Component
import java.nio.file.Paths

private const val REASON_NOT_MENTIONED = "Reason not mentioned"

/**
 * Reads MT940 records from input directory, checks their validity and writes the invalid records with their reasons
 * into the report writers
 */
@Component
class CustomerStatementProcessor(
    private val directoryReader: DirectoryReader,
    private val validators: List<RecordValidator>,
    private val reportWriters: List<ReportWriter>,
) {

    /**
     * Generates report on the input directory by using the existing validators
     * @param inputDirectoryPath
     */
    fun generateReportForDirectory(inputDirectoryPath: String) {
        val recordsWithViolations =
            readRecordsFromDirectory(inputDirectoryPath)
                .map { getRecordViolations(it) }
                .filter { it.hasViolations() }

        reportWriters.forEach {
            it.write(recordsWithViolations)
        }
    }

    private fun getRecordViolations(record: MT940Record): RecordViolations =
        RecordViolations(record).apply {
            validators.forEach { validator ->
                validator.validate(record)
                    .takeUnless { it.isValid() }
                    ?.let { addViolation(it.reason ?: REASON_NOT_MENTIONED) }
            }
        }

    private fun readRecordsFromDirectory(path: String) = directoryReader.readAll(Paths.get(path))
}
package nl.rabobank.assignment

import nl.rabobank.assignment.models.MT940Record
import nl.rabobank.assignment.readers.CSVReader
import nl.rabobank.assignment.readers.DirectoryReader
import nl.rabobank.assignment.readers.XMLReader
import nl.rabobank.assignment.report.ConsoleReportWriter
import nl.rabobank.assignment.report.ReportWriter
import nl.rabobank.assignment.validators.END_BALANCE_INCONSISTENCY
import nl.rabobank.assignment.validators.EndBalanceValidator
import nl.rabobank.assignment.validators.REFERENCE_UNIQUENESS_VIOLATION
import nl.rabobank.assignment.validators.UniqueReferenceValidator
import nl.rabobank.assignment.validators.models.RecordViolations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CustomerStatementProcessorTest {

    private lateinit var customerStatementProcessor: CustomerStatementProcessor
    private lateinit var reportWriter: ReportWriter
    private lateinit var lastInvalidRecords: List<RecordViolations>

    @BeforeEach
    fun setup() {
        reportWriter = ReportWriter { invalidRecords -> lastInvalidRecords = invalidRecords }
        customerStatementProcessor = CustomerStatementProcessor(
            DirectoryReader(listOf(CSVReader(), XMLReader())),
            listOf(EndBalanceValidator(), UniqueReferenceValidator()),
            listOf(reportWriter, ConsoleReportWriter())
        )
    }

    @Test
    fun `generate report`() {
        customerStatementProcessor.generateReportForDirectory(
            this::class.java.getResource("/unit-input/")!!.toURI().path
        )
        assertEquals(2, lastInvalidRecords.size)
        assertTrue(
            lastInvalidRecords.contains(RecordViolations(
                MT940Record(
                    181688,
                    "NL90ABNA0585647886",
                    "Flowers for Jan Theuß",
                    BigDecimal("75.39"),
                    "-31.75",
                    BigDecimal("42.64")
                )
            ).apply {
                addViolation(END_BALANCE_INCONSISTENCY)
                addViolation(REFERENCE_UNIQUENESS_VIOLATION)
            })
        )
        assertTrue(
            lastInvalidRecords.contains(RecordViolations(
                MT940Record(
                    112806,
                    "NL43AEGO0773393871",
                    "Subscription from Daniël Theuß",
                    BigDecimal("102.33"),
                    "+11.49",
                    BigDecimal("114.82")
                )
            ).apply { addViolation(END_BALANCE_INCONSISTENCY) })
        )
    }
}
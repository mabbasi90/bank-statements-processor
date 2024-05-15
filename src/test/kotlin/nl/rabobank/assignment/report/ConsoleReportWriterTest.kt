package nl.rabobank.assignment.report

import nl.rabobank.assignment.models.MT940Record
import nl.rabobank.assignment.validators.models.RecordViolations
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ConsoleReportWriterTest {

    @Test
    fun `writing just to be sure there is no error`() {
        ConsoleReportWriter().write(
            listOf(
                RecordViolations(
                    MT940Record(
                        137758,
                        "NL93ABNA0585619023",
                        "Tickets for Daniël King",
                        BigDecimal("97.56"),
                        "+46.41",
                        BigDecimal("143.97")
                    )
                ).apply { addViolation("there's something wrong with this record, but idk what is it...") },
                RecordViolations(
                    MT940Record(
                        131254,
                        "NL93ABNA0585619023",
                        "Candy from Vincent de Vries",
                        BigDecimal(5429),
                        "-939",
                        BigDecimal(6368)
                    )
                ).apply {
                    addViolation("first problem")
                    addViolation("second problem")
                }
            )
        )
    }
}
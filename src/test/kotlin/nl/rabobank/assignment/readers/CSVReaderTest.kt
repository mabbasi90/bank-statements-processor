package nl.rabobank.assignment.readers

import nl.rabobank.assignment.exceptions.RaboNotRegularFileException
import nl.rabobank.assignment.models.MT940Record
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.io.path.toPath

class CSVReaderTest {

    private val csvReader = CSVReader()

    private val sampleRecord = MT940Record(
        137758,
        "NL93ABNA0585619023",
        "Tickets for Daniël King",
        BigDecimal("97.56"),
        "+46.41",
        BigDecimal("143.97")
    )

    @Test
    fun `read all CSV records`() {
        val records = csvReader.readAll(this::class.java.getResource("/unit-input/records.csv")!!.toURI().toPath())
        assertEquals(3, records.size)
        assertEquals(sampleRecord, records[1])
    }

    @Test
    fun `throw an exception in case if getting a directory as input`() {
        assertThrows(RaboNotRegularFileException::class.java) {
            csvReader.readAll(
                this::class.java.getResource("/unit-input/")!!.toURI().toPath()
            )
        }
    }

    @Test
    fun `skip a bad row`() {
        val records = csvReader.readAll(this::class.java.getResource("/bad-input/records.csv")!!.toURI().toPath())
        assertEquals(2, records.size)
        assertEquals(sampleRecord, records[0])
    }

    @Test
    fun `passing empty reference`() {
        val records =
            csvReader.readAll(this::class.java.getResource("/bad-input/empty-reference.csv")!!.toURI().toPath())
        assertEquals(0, records.size)
    }
}

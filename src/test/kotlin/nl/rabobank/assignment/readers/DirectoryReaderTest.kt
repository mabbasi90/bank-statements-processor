package nl.rabobank.assignment.readers

import nl.rabobank.assignment.exceptions.RaboDuplicateInputExtension
import nl.rabobank.assignment.exceptions.RaboNotDirectoryException
import nl.rabobank.assignment.models.MT940Record
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.io.path.toPath

class DirectoryReaderTest {

    private val directoryReader = DirectoryReader(listOf(CSVReader(), XMLReader()))

    @Test
    fun `read all records and check both files`() {
        val records = directoryReader.readAll(this::class.java.getResource("/unit-input/")!!.toURI().toPath())
        assertEquals(
            6,
            records.size
        )
        assertTrue(
            records.contains(
                MT940Record(
                    137758,
                    "NL93ABNA0585619023",
                    "Tickets for Daniël King",
                    BigDecimal("97.56"),
                    "+46.41",
                    BigDecimal("143.97")
                )
            )
        )
        assertTrue(
            records.contains(
                MT940Record(
                    181688,
                    "NL90ABNA0585647886",
                    "Flowers for Jan Theuß",
                    BigDecimal("75.39"),
                    "-31.75",
                    BigDecimal("42.64")
                )
            )
        )
    }

    @Test
    fun `throw an exception when input is not a directory`() {
        assertThrows(RaboNotDirectoryException::class.java) {
            directoryReader.readAll(
                this::class.java.getResource("/unit-input/records.csv")!!.toURI().toPath()
            )
        }
    }

    @Test
    fun `throw an exception when there are duplicate extensions`() {
        assertThrows(RaboDuplicateInputExtension::class.java) {
            DirectoryReader(listOf(CSVReader(), CSVReader()))
        }
    }
}
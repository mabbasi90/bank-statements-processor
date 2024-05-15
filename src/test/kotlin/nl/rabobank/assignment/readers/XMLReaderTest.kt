package nl.rabobank.assignment.readers

import nl.rabobank.assignment.exceptions.RaboNotRegularFileException
import nl.rabobank.assignment.exceptions.RaboXMLParseException
import nl.rabobank.assignment.models.MT940Record
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.io.path.toPath

class XMLReaderTest {

    private val xmlReader = XMLReader()

    @Test
    fun `read all XML records`() {
        val records = xmlReader.readAll(this::class.java.getResource("/unit-input/records.xml")!!.toURI().toPath())
        assertEquals(3, records.size)
        assertEquals(
            MT940Record(
                181688,
                "NL90ABNA0585647886",
                "Flowers for Jan Theuß",
                BigDecimal("75.39"),
                "-31.75",
                BigDecimal("42.64")
            ),
            records[1]
        )
    }

    @Test
    fun `throw an exception in case of getting a directory as input`() {
        assertThrows(RaboNotRegularFileException::class.java) {
            xmlReader.readAll(this::class.java.getResource("/unit-input/")!!.toURI().toPath())
        }
    }

    @Test
    fun `throw exception in case of bad input`() {
        assertThrows(RaboXMLParseException::class.java) {
            xmlReader.readAll(this::class.java.getResource("/bad-input/records.xml")!!.toURI().toPath())
        }
    }
}
package nl.rabobank.assignment

import nl.rabobank.assignment.exceptions.RaboXMLParseException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class IntegrationTest {

    @Test
    fun `test main method`() {
        assertDoesNotThrow { main(arrayOf(this::class.java.getResource("/integration-input/")!!.path)) }
    }

    @Test
    fun `test main method with bad input`() {
        assertThrows(RaboXMLParseException::class.java) {
            main(
                arrayOf(
                    this::class.java.getResource("/bad-input/")!!.path
                )
            )
        }
    }

    @Test
    fun `test main method with no input`() {
        assertThrows(IllegalArgumentException::class.java) {
            main(arrayOf())
        }
    }
}
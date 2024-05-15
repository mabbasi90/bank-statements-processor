package nl.rabobank.assignment.validators

import nl.rabobank.assignment.models.MT940Record
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class EndBalanceValidatorTest {

    private lateinit var endBalanceValidator: EndBalanceValidator

    @BeforeEach
    fun setup() {
        endBalanceValidator = EndBalanceValidator()
    }

    @Test
    fun `checking a valid record`() {
        val recordValidity = endBalanceValidator.validate(
            MT940Record(
                123, "NL09ABNA214124", "some description", BigDecimal(10), "+10", BigDecimal(20)
            )
        )
        assertTrue(recordValidity.isValid())
        assertNull(recordValidity.reason)
    }

    @Test
    fun `checking an invalid record`() {
        val recordValidity = endBalanceValidator.validate(
            MT940Record(
                123, "NL09ABNA214124", "some description", BigDecimal(10), "+10", BigDecimal(25)
            )
        )
        assertFalse(recordValidity.isValid())
        assertEquals(END_BALANCE_INCONSISTENCY, recordValidity.reason)
    }
}
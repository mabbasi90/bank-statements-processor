package nl.rabobank.assignment.validators

import nl.rabobank.assignment.models.MT940Record
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UniqueReferenceValidatorTest {

    private lateinit var uniqueReferenceValidator: UniqueReferenceValidator

    @BeforeEach
    fun setup() {
        uniqueReferenceValidator = UniqueReferenceValidator()
    }

    @Test
    fun `checking a valid record`() {
        val recordValidity = uniqueReferenceValidator.validate(
            MT940Record(
                123, "NL09ABNA214124", "some description", BigDecimal(10), "+10", BigDecimal(20)
            )
        )
        assertTrue(recordValidity.isValid())
        assertNull(recordValidity.reason)
    }

    @Test
    fun `checking an invalid record with repeating reference`() {
        var recordValidity = uniqueReferenceValidator.validate(
            MT940Record(
                123, "NL09ABNA214124", "some description", BigDecimal(10), "+10", BigDecimal(20)
            )
        )
        assertTrue(recordValidity.isValid())
        assertNull(recordValidity.reason)
        recordValidity = uniqueReferenceValidator.validate(
            MT940Record(
                1234, "NL09ABNA214124", "some other description", BigDecimal(30), "+10", BigDecimal(40)
            )
        )

        assertTrue(recordValidity.isValid())
        assertNull(recordValidity.reason)

        recordValidity = uniqueReferenceValidator.validate(
            MT940Record(
                123, "NL11ABNA21243124", "some other description", BigDecimal(10), "-10", BigDecimal(0)
            )
        )

        assertFalse(recordValidity.isValid())
        assertEquals(REFERENCE_UNIQUENESS_VIOLATION, recordValidity.reason)
    }
}
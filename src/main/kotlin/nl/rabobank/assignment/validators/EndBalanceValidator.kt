package nl.rabobank.assignment.validators

import nl.rabobank.assignment.models.MT940Record
import nl.rabobank.assignment.validators.models.RecordValidity
import org.springframework.stereotype.Component
import java.math.BigDecimal

const val END_BALANCE_INCONSISTENCY = "End balance is inconsistent"

/**
 * Checks the end balance of a MT940 record based on its start balance and mutation
 */
@Component
class EndBalanceValidator : RecordValidator {

    companion object {
        private val minusOneBigDecimal = BigDecimal(-1)
    }

    override fun validate(record: MT940Record): RecordValidity =
        RecordValidity(if (!isValid(record)) END_BALANCE_INCONSISTENCY else null)

    private fun isValid(record: MT940Record) =
        record.startBalance.add(getMutationInBigDecimal(record)) == record.endBalance

    private fun getMutationInBigDecimal(record: MT940Record) = getMutationSign(record) * getMutationValue(record)

    private fun getMutationSign(record: MT940Record): BigDecimal =
        if (record.mutation.startsWith("+")) BigDecimal.ONE else minusOneBigDecimal

    private fun getMutationValue(record: MT940Record): BigDecimal = record.mutation.trimStart('-', '+').toBigDecimal()
}
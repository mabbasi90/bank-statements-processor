package nl.rabobank.assignment.models

import java.math.BigDecimal

/**
 * Simplified data model for the MT940 format in SWIFT
 */
data class MT940Record(
    val reference: Long,
    val accountNumber: String,
    val description: String?,
    val startBalance: BigDecimal,
    val mutation: String,
    val endBalance: BigDecimal
)
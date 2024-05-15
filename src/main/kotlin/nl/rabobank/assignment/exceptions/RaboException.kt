package nl.rabobank.assignment.exceptions

/**
 * Basic exception for the project
 */
open class RaboException(message: String, e: Exception? = null) : Exception(message, e)

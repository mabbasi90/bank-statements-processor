package nl.rabobank.assignment.exceptions

/**
 * When an expected field is not available in the input
 */
class RaboFieldNotAvailable(message: String, e: Exception? = null) : RaboException(message, e)

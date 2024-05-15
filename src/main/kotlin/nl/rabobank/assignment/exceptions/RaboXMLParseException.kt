package nl.rabobank.assignment.exceptions

/**
 * Exception is thrown if there's any parse errors in the MT940 xml format
 */
class RaboXMLParseException(message: String, e: Exception? = null) : RaboException(message, e)

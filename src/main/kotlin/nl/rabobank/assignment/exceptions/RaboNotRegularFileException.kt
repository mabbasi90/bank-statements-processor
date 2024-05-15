package nl.rabobank.assignment.exceptions

/**
 * Exception is thrown when the input path is not a regular file
 */
class RaboNotRegularFileException(message: String, e: Exception? = null) : RaboException(message, e)

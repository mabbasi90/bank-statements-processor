package nl.rabobank.assignment.exceptions

/**
 * Exception is thrown when the input path is not a directory
 */
class RaboNotDirectoryException(message: String, e: Exception? = null) : RaboException(message, e)

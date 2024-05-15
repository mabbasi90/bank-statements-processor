package nl.rabobank.assignment.exceptions

/**
 * Exception is thrown by {@link nl.rabobank.assignment.readers.DirectoryReader} when get same extension from multiple
 * readers
 */
class RaboDuplicateInputExtension(message: String, e: Exception? = null) : RaboException(message, e)

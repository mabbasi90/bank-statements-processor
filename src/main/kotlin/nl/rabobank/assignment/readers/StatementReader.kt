package nl.rabobank.assignment.readers

import nl.rabobank.assignment.models.MT940Record
import java.nio.file.Path

/**
 * Interface for working with readers of records with MT940 format
 */
interface StatementReader {

    fun readAll(path: Path): List<MT940Record>

    /**
     * File extension which is supported by the reader, must be unique
     */
    fun getExtension(): String
}

package nl.rabobank.assignment.readers

import nl.rabobank.assignment.exceptions.RaboDuplicateInputExtension
import nl.rabobank.assignment.exceptions.RaboNotDirectoryException
import nl.rabobank.assignment.models.MT940Record
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.io.path.isDirectory

private const val DIRECTORY_EXTENSION = "directory"

/**
 * Reads all the MT940 records of the supported file formats in a directory. Currently, only CSV and XML formats are
 * supported
 */
@Component
class DirectoryReader(readerImpls: List<StatementReader>) : StatementReader {

    private val readersByExtension: MutableMap<String, StatementReader> = mutableMapOf()

    init {
        readerImpls.forEach {
            if (readersByExtension.containsKey(it.getExtension())) {
                logger.error("Duplicate extension in the readers ${it.getExtension()}, overriding the previous one.")
                throw RaboDuplicateInputExtension("Extensions in the readers must be unique.")
            }
            readersByExtension[it.getExtension()] = it
        }
    }


    override fun readAll(path: Path): List<MT940Record> {
        if (!path.isDirectory()) {
            throw RaboNotDirectoryException("Path $path is not a directory")
        }
        val records = mutableListOf<MT940Record>()
        path.toFile().listFiles()?.forEach {
            val extension = it.extension.lowercase()
            if (it.isDirectory || !readersByExtension.containsKey(extension)) {
                logger.warn("Input $it is not supported.")
                return@forEach
            }
            readersByExtension[extension]?.let { reader -> records.addAll(reader.readAll(it.toPath())) }
        }
        return records
    }

    override fun getExtension() = DIRECTORY_EXTENSION
}
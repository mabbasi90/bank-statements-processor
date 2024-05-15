package nl.rabobank.assignment.readers

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import nl.rabobank.assignment.exceptions.RaboFieldNotAvailable
import nl.rabobank.assignment.exceptions.RaboNotRegularFileException
import nl.rabobank.assignment.models.MT940Record
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.io.path.isRegularFile

private const val CSV_EXTENSION = "csv"

/**
 * Reads all the MT940 records from a CSV file
 */
@Component
class CSVReader : StatementReader {

    override fun readAll(path: Path): List<MT940Record> {
        if (!path.isRegularFile()) {
            throw RaboNotRegularFileException("Path $path is not a regular file.")
        }
        return csvReader { charset = Charsets.ISO_8859_1.name() }.open(path.toFile().inputStream()) {
            readAllWithHeaderAsSequence().map {
                try {
                    MT940Record(
                        it["Reference"]?.toLong()
                            ?: throw RaboFieldNotAvailable("Field Reference is not available in the row"),
                        it["Account Number"]?.trim()
                            ?: throw RaboFieldNotAvailable("Field Account number is not available in the row"),
                        it["Description"],
                        it["Start Balance"]?.toBigDecimal()
                            ?: throw RaboFieldNotAvailable("Field Start Balance is not available in the row"),
                        it["Mutation"]
                            ?: throw RaboFieldNotAvailable("Field Mutation is not available in the row"),
                        it["End Balance"]?.toBigDecimal()
                            ?: throw RaboFieldNotAvailable("Field End Balance is not available in the input")
                    )
                } catch (e: Exception) {
                    logger.warn("Error in reading the row $it", e)
                    null
                }
            }.filterNotNull().toList()
        }
    }

    override fun getExtension() = CSV_EXTENSION
}
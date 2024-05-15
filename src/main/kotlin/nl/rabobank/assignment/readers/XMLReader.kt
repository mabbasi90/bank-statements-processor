package nl.rabobank.assignment.readers

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import nl.rabobank.assignment.exceptions.RaboNotRegularFileException
import nl.rabobank.assignment.exceptions.RaboXMLParseException
import nl.rabobank.assignment.models.MT940Record
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.io.path.isRegularFile


private const val XML_EXTENSION = "xml"

/**
 * Reads all the MT940 records from an XML file
 */
@Component
class XMLReader : StatementReader {

    private val xmlMapper = XmlMapper(JacksonXmlModule()).registerKotlinModule()

    override fun readAll(path: Path): List<MT940Record> {
        if (!path.isRegularFile()) {
            throw RaboNotRegularFileException("Path $path is not a regular file.")
        }
        try {
            return xmlMapper.readValue<List<MT940Record>>(path.toFile().inputStream())
        } catch (e: Exception) {
            throw RaboXMLParseException("Error in parsing the XML input $path", e)
        }
    }

    override fun getExtension() = XML_EXTENSION

}
package nl.rabobank.assignment

import org.springframework.context.annotation.AnnotationConfigApplicationContext

private const val BASE_PACKAGE = "nl.rabobank"

fun main(args: Array<String>) {
    require(args.size == 1) { "Usage: [bank-statements-directory]" }
    getCustomerStatementProcessorBean().generateReportForDirectory(args[0])
}

private fun getCustomerStatementProcessorBean() =
    AnnotationConfigApplicationContext(BASE_PACKAGE).getBean(CustomerStatementProcessor::class.java)


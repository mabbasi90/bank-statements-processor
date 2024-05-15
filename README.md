# Bank Statements Processor

This module reads CSV and XML files containing bank statement records in MT940 format, validates them, and generates a
report to show the invalid records with the reasons for their problems.

## Usage

To use it, you must pass the path of exactly one directory containing the input files to it. It skips subdirectories and
the files it does not support(logs the file addresses) and processes the CSV and XML files. For Example:

```shell
java -jar bank-statements-processor.jar ./input
```

or with docker:

```shell
docker run -v [input directory]:/app/input bank-statements-processor
```

You have to mount your input directory into /app/input in the image.

## Build

You can either build the jar file with maven:

```shell
./mvnw clean package
```

or if you want to skip the unit tests:

```shell
./mvnw clean package -DskipTests
```

or build the docker image:

```shell
docker build -t bank-statements-processor . 
```

## Design

The application is designed to consider the Open-Closed principle for the new type of readers, validators, and report
writers. It means if you need to add any new formats, validations, or report types, you only need to add a new
implementation of one of the interfaces and specify a bean for your implementation(either defining the bean yourself or
adding @Component annotation). The bean will be injected into the application and will be used. Without any need to
modify the existing code.</br>
It's worth mentioning that you cannot override an already existing implementation for a reader, but you can add a new
one.

### Notable packages and classes

#### Readers

There are multiple reader classes currently developed in the project that implement
the [StatementReader](src/main/kotlin/nl/rabobank/assignment/readers/StatementReader.kt) interface. One for reading CSV
files, one for XML files, and one for reading all files in a directory and parsing the supported files which is
called [Directory Reader](src/main/kotlin/nl/rabobank/assignment/readers/DirectoryReader.kt). At the high level, only
the Directory Reader is used to scan an input directory and return all the records of all the files in that
directory. It scans all the files and parse them based on their extension.</br>
To add a new type of input, you only need to implement a new reader, set the extension it supports, and use @Component
annotation over it. The Directory Reader will load it and parse the new type too.

#### Validators

Two validators are currently developed, by
implementing [RecordValidator](src/main/kotlin/nl/rabobank/assignment/validators/RecordValidator.kt) interface. One to
validate the end balances and one to validate the uniqueness of the references. The End Balance Validator is stateless
and the Unique Reference Validator is stateful.

#### Report Writer

There's only one report writer in the project right now, but by defining the Report Writer interface and creating a
bean, it's possible to use new ones too.

#### Customer Statement Processor

The [CustomerStatementProcessor](src/main/kotlin/nl/rabobank/assignment/CustomerStatementProcessor.kt) is the class
that binds all parts of the project to each other and creates the end-to-end pipeline to read inputs, validate them, and
generate the report. This class uses the Directory Reader to read inputs, loads all implementations of the Record
Validator to validate the records, and sends the invalid records to all report writers to generate the reports.

### Assumptions

1. Descriptions have escape characters in case of using any special characters, and we don't have to worry about the
   parsing
2. Input IBANs are validated and we don't need to validate them
3. The type of references is Long
4. The sum of all input file sizes is small enough to be loaded into the memory
5. We are validating files with each other to find repetitive reference numbers
6. In CSV format, non-parsable rows are skipped
7. There's no need to support recursive subdirectories
8. The input files are in ISO-8859-1 encoding

### Input format

Currently, two types are supported, CSV and XML.

#### CSV

```csv
Reference,Account Number,Description,Start Balance,Mutation,End Balance
112806,NL27SNSB0917829871,Subscription from Jan Dekker,28.95,-19.44,9.51
```

#### XML

```xml

<records>
    ...
    <record reference="138932">
        <accountNumber>NL90ABNA0585647886</accountNumber>
        <description>Flowers for Richard Bakker</description>
        <startBalance>94.9</startBalance>
        <mutation>+14.63</mutation>
        <endBalance>109.53</endBalance>
    </record>
    ...
</records>
```

## Decisions and alternatives

Because there are no specific functional and non-functional requirements defined for the project (especially about
performance), this module is developed with a basic non-concurrent solution to maintain simplicity. But it's
possible to add additional features like concurrency to it through some changes. However, we must consider that adding
concurrency and using threads/coroutines does not always improve the performance. Sometimes, having a lot of context
switches, using concurrent collections, and synchronization mechanisms makes the performance worse than a non-concurrent
solution. This depends on the size and type of input, the type of validation, and even the type of disk we are using(HDD
or SSD).

### Input

Currently, the module reads all files sequentially and loads all the records into the memory. But for the CSV format,
it's possible to read it line by line through an InputStream and process the records individually. Also, we can read
input files concurrently(If we're reading input files from HDD disks, it may be worse in performance, but with SSDs, it
can be much faster). But these solutions, need additional functionalities to coordinate and synchronize the
threads. For example adding a BlockingQueue for the threads to put their parsing output into it(I'm assuming that we
need to find repetitive records in all the files, not just each one separately). Then there must be some threads to
validate and print invalid records through the report writers.</br>
There's also another solution to read and process each file completely standalone in an exclusive thread, but with this
solution, we can't find the repetitive references among multiple files, and it's only good for a requirement that we
want to be sure about the uniqueness of references in each file.</br>
It is also worth mentioning that if we switch to the solution to parse each file in a separate thread, we have to create
a factory class that can create [StatementReaders](src/main/kotlin/nl/rabobank/assignment/readers/StatementReader.kt)
based on the input file extension, and we cannot load the readers through Spring Beans.

### Validation

The most important problem in making validations parallel is that if we want to find repeating references in all files,
we have to check them with a single concurrent HashSet(getting through ConcurrentHashMap.newKeySet()), which can reduce
the performance if we have a lot of threads. We don't have this problem with the End Balance Validator because it's
stateless.

### Scaling horizontally through a distributed architecture or Map/Reduce

We can achieve scalability for this project through multiple changes. The first one is multi-threading which is
explained already. But we can also scale this application by creating a distributed system to scale the system
horizontally. To achieve this, one solution is to break the application into two submodules. One reads and parses
the inputs and puts the records on a messaging broker's topic like Kafka and the other one consumes the records from the
topic to validate them(we can see the reports in the standard console of each instance of this module, or we can also
send the invalid records to another topic to print them). By using the reference as the message key of Kafka, we can
partition the topic in a way that we can be sure that the records with the same reference always will go to the same
consumer(here we also have to handle the failures and use a way to share the state of Unique Reference Validator, for
example using Redis as shared state store)</br>
Another solution for the inputs at the level of big data is using Map/Reduce(with Spark or Hadoop MR or a cloud
solution). To check the uniqueness of the references we can create an MR job by parsing and partitioning the
records by their references and a value of 1 in the Map task and aggregating the values in the reduce task. And if we
have a record with 2, it means that we have repetitive references. We can also easily validate the end balance in both
Map or Reduce task. Or if we use spark, we can easily check in a Spark function.


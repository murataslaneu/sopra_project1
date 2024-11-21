import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser

tasks.register("verify") {
    dependsOn(tasks.test)
    doLast {
        val testResultsDir = layout.buildDirectory.dir("test-results/test")
        val xmlFiles = testResultsDir.get().asFile.listFiles { file -> file.extension == "xml" }
        val parser = XmlParser()
        xmlFiles?.forEach { xmlFile ->
            println("Processing ${xmlFile.name}")
            val parsedXml = parser.parse(xmlFile)
            val suit = parsedXml.parseTestSuite()
            val failedCases = suit?.cases?.filter { it.failure != null }
            val failed = failedCases?.any { it.failure?.type != "org.opentest4j.AssertionFailedError" } ?: true
            println(failedCases)
            if (failed) {
                throw GradleException()
            }
        }
    }
}

fun Node.parseTestSuite(): TestSuite? {
    val name = this.name().toString()
    if (name == "testsuite") {
        val values = this.value() as? NodeList ?: return null
        val testCases = values.filterIsInstance<Node>().mapNotNull { it.parseTestCase() }
        return TestSuite(cases = testCases)
    }
    return null
}

fun Node.parseTestCase(): TestCase? {
    val name = this.name().toString()
    if (name == "testcase") {
        val values = this.value() as? NodeList ?: return null
        val failure = values.filterIsInstance<Node>().mapNotNull { it.parseFailure() }.firstOrNull()
        return TestCase(failure = failure)
    }
    return null
}

fun Node.parseFailure(): Failure? {
    val name = this.name().toString()
    if (name == "failure") {
        val type = this.attributes()["type"]?.toString() ?: return null
        return Failure(type = type)
    }
    return null
}

data class TestSuite(
    val cases: List<TestCase>,
)

data class TestCase(
    val failure: Failure?
)

data class Failure(
    val type: String,
)

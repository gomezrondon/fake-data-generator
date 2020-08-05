
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.io.File
import java.util.*


//https://aseigneurin.github.io/2018/08/01/kafka-tutorial-1-simple-producer-in-kotlin.html
//https://mockaroo.com/schemas/252080
//https://www.kotlinresources.com/library/kotlin-csv/
fun main(arg: Array<String>) {
   var producer= createProducer("127.0.0.1:9092")
    val kafkaTopic = arg[0]   //personsTopic

    val jsonMapper = ObjectMapper().apply {
        registerKotlinModule()
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        setDateFormat(StdDateFormat())
    }

    val list = fakeDataToGenerate()

    var pair = getInterval(list)
    var start = pair.first
    var step = pair.second
    var delay:Long = 2 //sec.

    for (i in start..1000_000 step step) {
        var tempJson = generateRecord(list)
        tempJson = tempJson.replace("##PLACE_HOLDER##", i.toString())
        sendJsonToKafkaTopic(tempJson, producer, kafkaTopic)
        Thread.sleep(delay * 1000L)
    }

}

private fun sendJsonToKafkaTopic(
    jsonvalue: String,
    producer: Producer<String, String>,
    kafkaTopic: String
) {
    val futureResult = producer.send(ProducerRecord(kafkaTopic, jsonvalue))
    print(jsonvalue)
    println(futureResult.get())
}

/*
{   "userid": 1000 , "firstname": "Heidi" , "lastname": "Smith" , "countrycode": "GB" , "rating": "3.4"  }
{   "userid": 1001 , "firstname": "Carol" , "lastname": "Smith" , "countrycode": "GB" , "rating": "3.4"  }
{   "userid": 1002 , "firstname": "Frank" , "lastname": "Smith" , "countrycode": "GB" , "rating": "4.4"  }
*/


private fun createProducer(brokers: String): Producer<String, String> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["key.serializer"] = StringSerializer::class.java.canonicalName
    props["value.serializer"] = StringSerializer::class.java.canonicalName
    return KafkaProducer<String, String>(props)
}

private fun generateFakeDataFromMockFile() {
    val rows: List<List<String>> = csvReader().readAll(File("MOCK_DATA.csv"))
    val hedears = listOf<String>("userid", "firstname", "lastname", "countrycode", "rating")

    rows.forEach { line ->
        var tempJson = "{ "
        line.forEachIndexed { index, field ->
            val isLastField = if (index + 1 == hedears.size) true else false
            val numeric = field.matches("-?\\d+(\\.\\d+)?".toRegex())
            val header = """ "${hedears[index]}":   """

            if (numeric) {
                tempJson += header + field
            } else {
                tempJson += header + """ "$field" """
            }

            if (!isLastField) {
                tempJson += ","
            }

        }
        tempJson += " }"
        println(tempJson)
    }
}





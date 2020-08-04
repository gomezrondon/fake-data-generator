
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
fun main() {
 //  var producer= createProducer("127.0.0.1:9092")


    val rows: List<List<String>> = csvReader().readAll(File("MOCK_DATA.csv"))
    val hedears = listOf<String>("userid", "firstname", "lastname", "countrycode","rating")

    rows.forEach { line ->
        var tempJson="{ "
         line.forEachIndexed { index, field ->
            val isLastField = if (index +1 == hedears.size) true else false
            val numeric = field.matches("-?\\d+(\\.\\d+)?".toRegex())
            val header = """ "${hedears[index]}":   """

             if (numeric) {
                 tempJson += header + field
             }else{
                 tempJson += header + """ "$field" """
             }

             if (!isLastField) {
                 tempJson+=","
             }

         }
        tempJson +=" }"
        println(tempJson)
    }

/*    val jsonMapper = ObjectMapper().apply {
        registerKotlinModule()
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        setDateFormat(StdDateFormat())
    }

    val fakePersonJson = jsonMapper.writeValueAsString(fakePerson)

    val futureResult = producer.send(ProducerRecord("personsTopic", fakePersonJson))
    val get = futureResult.get()

    println(fakePersonJson)
    println(get)*/
}


/*
private fun createProducer(brokers: String): Producer<String, String> {
    val props = Properties()
    props["bootstrap.servers"] = brokers
    props["key.serializer"] = StringSerializer::class.java.canonicalName
    props["value.serializer"] = StringSerializer::class.java.canonicalName
    return KafkaProducer<String, String>(props)
}
*/


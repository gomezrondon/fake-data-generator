import com.google.gson.Gson
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Paths


//https://attacomsian.com/blog/gson-read-json-file
fun main() {



}


fun fakeDataToGenerate(): List<Field> {
    val gson = Gson()
    val reader: Reader = Files.newBufferedReader(Paths.get("data.json"))
    val map = gson.fromJson<Map<*, *>>(reader, MutableMap::class.java)

    val fieldsMap = map.filterKeys { it!!.equals("fields") }

    val any:List<Any?>  = fieldsMap["fields"] as List<Any?>

    val toList = any.map { record ->
        //   println(record) //{name=userid, type={type=string, arg.properties={iteration={start=1000.0, step=1.0}}}}

        val replace = record.toString().replace("""\{|}""".toRegex(), " ")
            .replace("[", " ")
            .replace("]", " ")
            .split(",")
            .flatMap { it.split("*") }
            .flatMap { it.split("=") }
            .filter { it.isNotEmpty() }.toMutableList()
        // .map { it.replace("="," ") }

        replace.removeAt(2)
        replace.removeAt(2)
        replace.removeAt(2)
        replace.removeAt(2)

        var pro = Properties()
        replace.drop(2).forEachIndexed { index, word ->
            if (index == 0) {
                pro.type = word.trim()
            } else {
                pro.values.add(word.trim())
            }

        }

        var record = Field()
        replace.take(2).drop(1).forEach { record.name = it }
        record.properties = pro

        //  println(pro)
        //println(record)
        record
    }.toList()

    return toList

}


data class Properties(var type:String="", var values:MutableList<String> = mutableListOf())
data class Field(var name:String="",  var properties: Properties = Properties())
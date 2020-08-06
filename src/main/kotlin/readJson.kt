import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import java.io.File



//https://attacomsian.com/blog/gson-read-json-file
fun main() {
    fakeDataToGenerate2()


}
//http://jsonpath.herokuapp.com/?path=$..*
fun fakeDataToGenerate2(): List<Field> {
    val gson = Gson()
    val list2: MutableList<Field> = mutableListOf()

    val tempSize:JSONArray =JsonPath.parse(File("userprofile.avro"))
        .read("$..fields.size() ")
    val size = getValueInt(tempSize)

    for (i in 0 until size) {
       // println(">>>>>>>>>>>>>>>>< $i")
        val record:Any = JsonPath.parse(File("userprofile.avro"))
            .read("$..fields.[$i]")
        val toJson = gson.toJson(record)

        val tempName:JSONArray = JsonPath.parse(toJson).read("$..name")
        val name:String = getValueString(tempName)
        val iteration: JSONArray = JsonPath.parse(toJson).read("$..iteration")

        val field:Field =  if (iteration.isNotEmpty()) {
            val start:JSONArray = JsonPath.parse(iteration).read("$..start")
            val step:JSONArray = JsonPath.parse(iteration).read("$..step")
            val list = mutableListOf(getValueInt(start).toString(), getValueInt(step).toString())
            val prop = Properties2(type = "iteration", values =  list)
            Field(name= name, properties = prop)
        }else{
            val options:JSONArray = JsonPath.parse(toJson).read("$..options")
            val toList: MutableList<String> = options[0] as MutableList<String>
            val prop = Properties2(type = "options" , values = toList )
            Field(name= name, properties = prop)
        }

        list2.add(field)
    }


    return list2
}

private fun getValueString(tempName: JSONArray) = tempName[0] as String

private fun getValueInt(iteration: JSONArray) = iteration[0] as Int



data class Properties2(var type:String="", var values:MutableList<String> = mutableListOf())

data class Field(var name:String="",  var properties: Properties2 = Properties2())
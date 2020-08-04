import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

fun main() {

    val rnd = Random()
    val list = fakeDataToGenerate()


    var pair = getInterval(list)
    var start = pair.first
    var step = pair.second
    var delay:Long = 2 //sec.

    for (i in start..1000_000 step step) {
        var tempJson = generateRecord(list, rnd)
        tempJson = tempJson.replace("##PLACE_HOLDER##", i.toString())
        println(tempJson)
        Thread.sleep(delay * 1000L)
    }


}



private fun generateRecord(list: List<Field>, rnd: Random): String {
    var tempJson = " {  "
    list.forEachIndexed { index, it ->
        val isLastField = if (index + 1 == list.size) true else false

        val type = it.properties.type.trim()
        if (!type.equals("iteration")) {
            val randValue = getRamdomValue(it, rnd)
            tempJson += """ "${it.name}": "${randValue.trim()}" """
        }else{
            tempJson += """ "${it.name}": ##PLACE_HOLDER## """
        }

        if (!isLastField) {
            tempJson += ","
        }
    }

    tempJson += " }"
    return tempJson
}

private fun getInterval(list: List<Field>): Pair<Int, Int> {
    var start = 0
    var step = 0
    val Iterations = list.filter { it.properties.type == "iteration" }.firstOrNull()
    if (Iterations != null) {
        start = Iterations.properties.values[1].toFloat().toInt()
        step = Iterations.properties.values[3].toFloat().toInt()
    }
    return Pair(start, step)
}

private fun getRamdomValue(get: Field,   rnd: Random): String {
    var i1 = 0
    val size = get.properties.values.size
    i1 = rnd.nextInt(size)
    val randValue = get.properties.values[i1]
    return randValue
}
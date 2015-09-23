package main

import model.CompressedWord
import model.compress
import model.toJson
import model.wordList
import org.json.JSONArray
import utils.WordNetUtils
import utils.partitionBySize
import java.io.File
import java.util.*
import kotlin.util.measureTimeMillis

public fun main(args: Array<String>): Unit {
    println(measureTimeMillis {
        readFromJson().forEach { println(it) }
    })
}
private fun trek() {
    val words = WordNetUtils.iWords.wordList()
    words.compress().forEach { println(it) }
}
private fun outputJson() {
    WordNetUtils.iWords.wordList().compress(4).partitionBySize(1000).forEachIndexed { i, list ->
        File("jsonOutput/$i.json").writeText(list.toJson().toString(0))
        println("wrote file $i.json")
    }
}
private fun readFromJson(): LinkedHashMap<Int, CompressedWord> {
    val ret = LinkedHashMap<Int, CompressedWord>()
    File("jsonOutput").listFiles().map {
        val json = JSONArray(it.readText())
        (0..json.length() - 1).map {index -> json.optJSONObject(index) }.map { jsonObject ->
            if (jsonObject != null) CompressedWord(jsonObject) else null
        }.filterNotNull()
    }.flatten().sorted().forEach{ ret.put(it.id, it) }
    return ret
}
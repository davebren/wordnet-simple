package json

import model.CompressedWord
import model.compress
import model.toJson
import model.wordList
import org.json.JSONArray
import utils.WordNetUtils
import utils.partitionBySize
import java.io.File

public class JsonConverter { companion object {
public fun outputJson() {
    WordNetUtils.iWords.wordList().compress(4).partitionBySize(1000).forEachIndexed { i, list ->
        File("jsonOutput/$i.json").writeText(list.toJson().toString(0))
        println("wrote file $i.json")
    }
}
public fun readFromJson(): List<CompressedWord> {
    return File("jsonOutput").listFiles().map {
        val json = JSONArray(it.readText())
        (0..json.length() - 1).map {index -> json.optJSONObject(index) }.map { jsonObject ->
            if (jsonObject != null) CompressedWord(jsonObject) else null
        }.filterNotNull()
    }.flatten().sorted()
}
}}
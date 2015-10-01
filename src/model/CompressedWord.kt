package model

import edu.mit.jwi.item.POS
import org.json.JSONArray
import org.json.JSONObject
import sun.jvm.hotspot.utilities.WorkerThread
import utils.*
import java.awt
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

public open data class CompressedWord(val id: Int,
                                      val name: String,
                                      val definition: String,
                                      val pos: POS,
                                      val examples: List<String> = arrayListOf(),
                                      val synonyms: List<Int> = arrayListOf(),
                                      val antonyms: List<Int> = arrayListOf(),
                                      val hypernyms: List<Int> = arrayListOf(),
                                      val hyponyms: List<Int> = arrayListOf(),
                                      val holonyms: List<Int> = arrayListOf(),
                                      val meronyms: List<Int> = arrayListOf()): Comparable<CompressedWord> {
    constructor(json: JSONObject)
    : this(json.optInt(CompressedWord::id.name, 0),
            json.optString(CompressedWord::name.name, ""),
            json.optString(CompressedWord::definition.name, ""),
            WordNetUtils.posFromNumber(json.optInt(CompressedWord::pos.name, 0)),
            json.optJSONArray(CompressedWord::examples.name)?.toNonEmptyStringList() ?: arrayListOf<String>(),
            json.optJSONArray(CompressedWord::synonyms.name)?.toPositiveIntList() ?: arrayListOf<Int>(),
            json.optJSONArray(CompressedWord::antonyms.name)?.toPositiveIntList() ?: arrayListOf<Int>(),
            json.optJSONArray(CompressedWord::hypernyms.name)?.toPositiveIntList() ?: arrayListOf<Int>(),
            json.optJSONArray(CompressedWord::hyponyms.name)?.toPositiveIntList() ?: arrayListOf<Int>(),
            json.optJSONArray(CompressedWord::holonyms.name)?.toPositiveIntList() ?: arrayListOf<Int>(),
            json.optJSONArray(CompressedWord::meronyms.name)?.toPositiveIntList() ?: arrayListOf<Int>())
    public fun toJson(): JSONObject {
        val json = JSONObject().put(::id.name, id).put(::name.name, name).put(::definition.name, definition).put(::pos.name, pos.number)
        if (examples.isNotEmpty()) json.put(::examples.name, examples)
        if (synonyms.isNotEmpty()) json.put(::synonyms.name, synonyms)
        if (antonyms.isNotEmpty()) json.put(::antonyms.name, antonyms)
        if (hypernyms.isNotEmpty()) json.put(::hypernyms.name, hypernyms)
        if (hyponyms.isNotEmpty()) json.put(::hyponyms.name, hyponyms)
        if (holonyms.isNotEmpty()) json.put(::holonyms.name, holonyms)
        if (meronyms.isNotEmpty()) json.put(::meronyms.name, meronyms)
        return json
    }
    override fun compareTo(other: CompressedWord): Int {
        val decased = AlphaNum.compare(name.toLowerCase(), other.name.toLowerCase())
        return if (decased != 0) decased else AlphaNum.compare(name, name)
    }
}
public fun List<Word>.compress(numThreads: Int = 1): List<CompressedWord> {
    val executor = Executors.newFixedThreadPool(5)
    val partitionedOutput = (0..numThreads-1).map { arrayListOf<CompressedWord>() }
    this.partitionByNumPartitions(numThreads).forEachIndexed { i, list ->
        executor.execute(runnable { partitionedOutput.get(i).addAll(list.map {
            // the bottleneck.
            val compressedWord =
                    CompressedWord(it.id, it.name, it.definition, it.pos, it.examples,
                            it.synonyms.map { syn -> this.indexOf(syn) }.filter {index -> index != -1 },
                            it.antonyms.map { ant -> this.indexOf(ant) }.filter {index -> index != -1 },
                            it.hypernyms.map { hyp -> this.indexOf(hyp) }.filter {index -> index != -1},
                            it.hyponyms.map { hyp -> this.indexOf(hyp) }.filter {index -> index != -1},
                            it.holonyms.map { hol -> this.indexOf(hol) }.filter {index -> index != -1},
                            it.meronyms.map { hyp -> this.indexOf(hyp) }.filter {index -> index != -1})
            compressedWord
        })})
    }
    executor.shutdown()
    executor.awaitTermination(99, TimeUnit.DAYS)
    System.out.println("Finished all threads");

    return partitionedOutput.flatten()
}
public fun List<CompressedWord>.toJson(): JSONArray {
    val json = JSONArray()
    this.forEach { json.put(it.toJson()) }
    return json
}
public fun List<CompressedWord>.removeUpperCase(): List<CompressedWord> {
    val ret = this.filter { it.name.equals(it.name.toLowerCase()) }
    println("removeUppercase: ${this.size()} -> ${ret.size()}")
    return ret
}
public fun List<CompressedWord>.removeFirstCharNotRoman(): List<CompressedWord> {
    val ret = this.filter {it.name.first().toInt().twixt(97, 122)}
    println("removeFirstCharNotRoman: ${this.size()} -> ${ret.size()}")
    return ret
}
public fun Char.twixt(start: Char, end: Char): Boolean {
    return this.toInt() >= start.toInt() && this.toInt() <= end.toInt()
}
public fun Int.twixt(start: Int, end: Int): Boolean {
    return this >= start && this <= end
}
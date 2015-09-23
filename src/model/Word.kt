package model

import edu.mit.jwi.item.IWord
import edu.mit.jwi.item.POS
import org.json.JSONObject
import utils.*
import java.util.*

public open data class Word(val id: Int,
                            val name: String,
                            val definition: String,
                            val pos: POS,
                            val examples: List<String> = arrayListOf(),
                            val synonyms: MutableList<Word> = arrayListOf(),
                            val antonyms: MutableList<Word> = arrayListOf(),
                            val hypernyms: MutableList<Word> = arrayListOf(),
                            val hyponyms: MutableList<Word> = arrayListOf(),
                            val holonyms: MutableList<Word> = arrayListOf(),
                            val meronyms: MutableList<Word> = arrayListOf()) {

    constructor(iWordWrapper: IWordWrapper, child: Boolean = false, id: Int = 0)
    : this(id,
            iWordWrapper.name,
            iWordWrapper.definition,
            iWordWrapper.pos,
            iWordWrapper.examples) {
        if (!child) {
            synonyms.addAll(iWordWrapper.synonyms.map { Word(it, true) })
            antonyms.addAll(iWordWrapper.antonyms.map { Word(it, true) })
            hypernyms.addAll(iWordWrapper.hypernyms.map { Word(it, true) })
            hyponyms.addAll(iWordWrapper.hyponyms.map { Word(it, true) })
            holonyms.addAll(iWordWrapper.holonyms.map { Word(it, true) })
            meronyms.addAll(iWordWrapper.meronyms.map { Word(it, true) })
        }
    }
    override fun hashCode() = name.hashCode() * 31 + definition.hashCode()
    override fun equals(other: Any?): Boolean {
        val other = other as Word
        return name.equals(other.name) && definition.equals(other.definition)
    }
}
public fun List<Pair<IWord, POS>>.wordList(): List<Word> {
    return this.wrap().sorted().mapIndexed { i, iword -> Word(iword, false, i)}
}
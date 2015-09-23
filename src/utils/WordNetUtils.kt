package utils

import edu.mit.jwi.Dictionary
import edu.mit.jwi.IDictionary
import edu.mit.jwi.item.IWord
import edu.mit.jwi.item.POS
import java.io.File
import java.util.*

public open class WordNetUtils {
    companion object {
        public val dictionary: IDictionary by lazy {
            val it = Dictionary(File("dict-3.1"))
            it.open()
            it
        }

        public val iWords by lazy {
            val iWords = ArrayList<Pair<IWord, POS>>()
            POS.values().forEach { pos -> dictionary.getIndexWordIterator(pos).forEach {
                it.wordIDs.map { dictionary.getWord(it) }.forEach {
                    iWords.add(Pair(it, pos))
                }
            }}
            iWords
        }

        private val posMap: Map<Int, POS> by lazy {
            val map = HashMap<Int, POS>()
            POS.values().forEach { map.put(it.number, it) }
            map
        }

        public fun posFromNumber(number: Int): POS {
            return posMap.getOrElse(number, {POS.ADJECTIVE})
        }
    }
}
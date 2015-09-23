package utils

import edu.mit.jwi.item.IPointer
import edu.mit.jwi.item.IWord
import edu.mit.jwi.item.POS
import edu.mit.jwi.item.Pointer
import org.apache.commons.lang3.StringUtils

public class IWordWrapper(val iWord: IWord, val pos: POS): Comparable<IWordWrapper> {
    val name: String by lazy { iWord.lemma.replace("_", " ") }
    val definition: String by lazy { val endIndex = iWord.synset.gloss.indexOf("; \"")
        if (endIndex == -1) iWord.synset.gloss else iWord.synset.gloss.substring(0, endIndex)
    }
    val examples: List<String> by lazy { (StringUtils.substringsBetween(iWord.synset.gloss, "\"", "\"")?.asList() ?: emptyList<String>()).filter { it.contains(name)} }
    val synonyms: List<IWordWrapper> by lazy { iWord.synset.words.map { IWordWrapper(it, it.pos) }.filterNot { it.iWord.lemma.equals(iWord.lemma) }}
    val antonyms: List<IWordWrapper> by lazy { getLexicalRelations(Pointer.ANTONYM) }
    val hypernyms: List<IWordWrapper> by lazy { getSemanticRelations(Pointer.HYPERNYM, Pointer.HYPERNYM_INSTANCE) }
    val hyponyms: List<IWordWrapper> by lazy { getSemanticRelations(Pointer.HYPONYM, Pointer.HYPONYM_INSTANCE) }
    val holonyms: List<IWordWrapper> by lazy { getSemanticRelations(Pointer.HOLONYM_MEMBER, Pointer.HOLONYM_PART, Pointer.HOLONYM_SUBSTANCE) }
    val meronyms: List<IWordWrapper> by lazy { getSemanticRelations(Pointer.MERONYM_MEMBER, Pointer.MERONYM_PART, Pointer.MERONYM_SUBSTANCE) }

    private fun getLexicalRelations(pointer: IPointer): List<IWordWrapper> {
        return iWord.getRelatedWords(pointer).map { IWordWrapper(WordNetUtils.dictionary.getWord(it), pos) }
    }
    private fun getSemanticRelations(vararg pointers: IPointer): List<IWordWrapper> {
        return pointers.map {pointer ->
            iWord.synset.getRelatedSynsets(pointer).map { WordNetUtils.dictionary.getSynset(it).words.filter { relation -> !iWord.lemma.equals(relation.lemma) } }.flatten().map { relation -> IWordWrapper(relation, relation.pos) }
        }.flatten()
    }
    override fun compareTo(other: IWordWrapper): Int {
        val decased = AlphaNum.compare(iWord.lemma.toLowerCase(), other.iWord.lemma.toLowerCase())
        return if (decased != 0) decased else AlphaNum.compare(iWord.lemma, other.iWord.lemma)
    }
}

public fun List<Pair<IWord, POS>>.wrap(): List<IWordWrapper> {
    return this.mapIndexed { index, pair -> IWordWrapper(pair.first, pair.second) }
}
package utils

public fun <T> List<T>.partitionBySize(partitionSize: Int): List<List<T>> {
    return (0..Math.ceil((this.size().toDouble() / partitionSize.toDouble())).toInt() - 1).map {
        this.subList(it*partitionSize, Math.min(this.size(), (it+1)*partitionSize))
    }
}
public fun <T> List<T>.partitionByNumPartitions(partitionCount: Int): List<List<T>> {
    val partitionSize = this.size() / partitionCount
    var overflow = this.size() - partitionSize*partitionCount

    var offset = 0
    println("partitionByNumPartitions: $partitionCount,  $partitionSize, ${this.size().toDouble()}")
    return (0..(partitionCount -1)).map {
        val extra = if (overflow > 0) 1 else 0
        overflow--
        val startIndex = offset
        offset += partitionSize + extra
        println("partition $it: ($startIndex, $offset]")
        this.subList(startIndex, Math.min(this.size(), offset))
    }
}
package utils

import java.util.*

public fun <T> List<T>.partitionBySize(partitionSize: Int): List<List<T>> {
    return (0..Math.ceil((this.size().toDouble() / partitionSize.toDouble())).toInt() - 1).map {
        this.subList(it*partitionSize, Math.min(this.size(), (it+1)*partitionSize))
    }
}
public fun <T> List<T>.partitionByNumPartitions(partitionCount: Int): List<List<T>> {
    val partitionSize = Math.ceil(this.size().toDouble() / partitionCount.toDouble()).toInt()
    return (0..(partitionCount-1)).map { this.subList(it*partitionSize, Math.min(this.size(), (it + 1)*partitionSize)) }
}
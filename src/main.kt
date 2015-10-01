package main

import json.JsonConverter
import model.compress
import model.removeFirstCharNotRoman
import model.removeUpperCase
import model.wordList
import sqlite.SqliteStrategies
import utils.WordNetUtils
import kotlin.util.measureTimeMillis

public fun main(args: Array<String>): Unit {
    println(measureTimeMillis {
        SqliteStrategies.writeToSqliteSplit(200, JsonConverter.readFromJson().removeUpperCase().removeFirstCharNotRoman(), "reducedWordsPartitioned")
    })
}
private fun trek() {
    val words = WordNetUtils.iWords.wordList()
    words.compress().forEach { println(it) }
}
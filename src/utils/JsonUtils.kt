package utils

import org.json.JSONArray
import org.json.JSONString

public fun JSONArray.toPositiveIntList(): List<Int> {
    return (0..(this.length() - 1)).map { i -> this.optInt(i, -1) }.filter { j -> j >= 0 }
}
public fun JSONArray.toNonEmptyStringList(): List<String> {
    return (0..(this.length() -1)).map { i -> this.optString(i, "") }.filter { j -> !j.equals("") }
}
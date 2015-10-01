package sqlite

import json.JsonConverter
import model.CompressedWord
import utils.partitionByNumPartitions
import utils.partitionBySize
import java.sql.DriverManager

public class SqliteStrategies { companion object {
public fun writeToSqlite() {
    val connection = DriverManager.getConnection("jdbc:sqlite:words.sqlite")
    var statement = connection.createStatement()
    statement.executeUpdate("create table words (_id integer primary key autoincrement not null, "
            + "id integer not null, "
            + "name text not null, "
            + "json text not null)")
    statement.close()

    val insertSql = "INSERT INTO words(id,name,json) values (?, ?, ?);"
    var insertStatement = connection.prepareStatement(insertSql)

    JsonConverter.readFromJson().partitionBySize(1000).forEachIndexed { index, it ->
        it.forEach {
            insertStatement.setInt(1, it.id)
            insertStatement.setString(2, it.name)
            insertStatement.setString(3, it.toJson().toString(0))
            insertStatement.addBatch()
        }
        insertStatement.executeBatch()
        insertStatement = connection.prepareStatement(insertSql)
        println("inserted batch $index")
    }
}
public fun writeToSqliteSplit(tableCount: Int, words: List<CompressedWord>, name: String = "wordsPartitioned") {
    val connection = DriverManager.getConnection("jdbc:sqlite:$name.sqlite")
    words.partitionByNumPartitions(tableCount).forEachIndexed { tableName, partition ->
        val statement = connection.createStatement()
        statement.executeUpdate("create table t$tableName (_id integer primary key autoincrement not null, id integer not null, name text not null, json text not null)")
        statement.close()
        val insertStatement = connection.prepareStatement("insert into t$tableName(id, name, json) values (?, ?, ?);")
        partition.forEach {
            insertStatement.setInt(1, it.id)
            insertStatement.setString(2, it.name)
            insertStatement.setString(3, it.toJson().toString(0))
            insertStatement.addBatch()
        }
        insertStatement.executeBatch()
        println("created table $tableName")
    }
}
}}
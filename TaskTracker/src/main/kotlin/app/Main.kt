package app

import Helper
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class Main(private val taskTracker: TaskTracker) {
    var inputKodeTask: Int? = null

    fun mainApp() {
        // Database connection details
        val connectionUrl = "jdbc:mysql://localhost:3306/tasktracker_db"
        val user = "root"
        val password = ""

        DriverManager.getConnection(connectionUrl, user, password).use { connection ->
            if (isTableNotEmpty(connection)) {
                print("===================================================\n")
                print("TASK TRACKER \n1. Create\n2. Lihat list task\nMasukkan pilihan anda: ")
                val input: Int? = readLine()?.toInt()

                when (input) {
                    1 -> {
                        taskTracker.addRow(connection)
                        backMainState()
                    }

                    2 -> {
                        when (val resultData = taskTracker.showListTask(connection)) {
                            is Helper.Success -> {
                                val listData = resultData.data
                                listData.forEach {
                                    println("${it.id}. ${it.title}")
                                }
                            }

                            is Helper.Failed -> {
                                println("Error: ${resultData.errorMessage}")
                            }
                        }

                        print("pilih berdasarkan kode task(ketik 0 untuk kembali): ")

                        inputKodeTask = readLine()?.toInt()
                        when (inputKodeTask) {
                            0 -> backMainState()
                            else -> taskTracker.detailTask(connection, inputKodeTask!!, this)
                        }
                    }

                    else -> {
                        println("Pilihan tidak valid.")
                        backMainState()
                    }
                }
            } else {
                print("TASK TRACKER \n1. Create \nTask kosong, tekan 1 untuk tambah task: ")
                var input: Int? = readLine()?.toInt()
                if (input == 1) {
                    taskTracker.addRow(connection)
                }
            }
        }
    }

    fun isTableNotEmpty(connection: Connection): Boolean {

        val countSql = "SELECT COUNT(*) FROM tasks"

        connection.prepareStatement(countSql).use { statement ->
            val resultSet: ResultSet = statement.executeQuery()
            resultSet.next()
            val rowCount = resultSet.getInt(1)
            return rowCount > 0
        }
    }

    fun backMainState() {
        mainApp()
    }

}

fun main() {
    val taskTracker = TaskTracker()
    val main = Main(taskTracker)
    main.mainApp()
}








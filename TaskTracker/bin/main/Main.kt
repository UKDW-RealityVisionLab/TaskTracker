import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

var inputKodeTask: Int? = null
fun main() {
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
                    addRow(connection)
                    backMainState()
                }

                2 -> {
                    showListTask(connection)
                    print("pilih berdasarkan kode task(ketik 0 untuk kembali): ")

                    inputKodeTask = readLine()?.toInt()
                    when (inputKodeTask) {
                        0 -> backMainState()
                        else -> detailTask(connection, inputKodeTask!!)
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
                addRow(connection)
            }
        }
    }
}

//tambah task
fun addRow(connection: Connection) {
    // Insert statementnya
    print("---\nCreate Task\nMasukkan Judul: ")
    val insertSql = "INSERT INTO tasks (judul, deskripsi) VALUES (?, ?)"

    // Input data pengguna
    val judul: String? = readLine()
    print("Masukkan Deskripsi: ")
    val deskripsi: String? = readLine()

    // Memasukkan input pengguna kedalam insert statement
    connection.prepareStatement(insertSql).use { preparedStatement ->
        preparedStatement.setString(1, judul)
        preparedStatement.setString(2, deskripsi)
        preparedStatement.executeUpdate()
    }
    println("Data berhasil dimasukkan\n---")
}

//show list task
fun showListTask(connection: Connection) {
    val listQuery = "SELECT * FROM tasks"
    print("--\nList task anda:\n")

    connection.createStatement().use {
        val fetchList = it.executeQuery(listQuery)
        while (fetchList.next()) {
            val kodeTask = fetchList.getInt("kode_task")
            val titleTask = fetchList.getString("judul")
            println("$kodeTask. $titleTask ")
        }
    }
}

fun detailTask(connection: Connection, id :Int) {
    val getId= "SELECT * FROM tasks WHERE kode_task=$id"
    connection.createStatement().use {
        val resultId=it.executeQuery(getId)
        while (resultId.next()){
            val tittle= resultId.getString("judul")
            val desc=resultId.getString("deskripsi")
            print("----Detail task $tittle----\n")
            println("Judul:$tittle\nDeskripsi:$desc")
        }
        println("0. kembali\n1. edit\n2. hapus")
        print("masukkan pilihan anda sesuai angka untuk mengedit atau menghapus task:")
        val input:Int= readLine()!!.toInt()
        when (input) {
            0 -> showListTask(connection)
            1 -> {
                // Update task
                updateTask(connection, inputKodeTask)
                backMainState()

            }
            2 ->{deleteFun(connection,inputKodeTask)
            backMainState()
            }
        }
    }
}

fun updateTask(connection: Connection, id: Int?) {

    val selectSql = "SELECT * FROM tasks WHERE kode_task = ?"








        print("Masukkan Judul Baru: ")
        val newTitle: String? = readLine()
        print("Masukkan Deskripsi Baru: ")
        val newDesc: String? = readLine()
        val updateSql = "UPDATE tasks SET judul=?, deskripsi=? WHERE kode_task=?"

        val updateSqlCumaDeskripsi = "UPDATE tasks SET   deskripsi=? WHERE kode_task=?"
        val updateSqlCumaTitle = "UPDATE tasks SET  judul=? WHERE kode_task=?"

    if (newTitle?.isNotBlank() == true && newDesc?.isNotBlank() == true) {
        // Update both title and description
        connection.prepareStatement(updateSql).use { preparedStatement ->
            preparedStatement.setString(1, newTitle)
            preparedStatement.setString(2, newDesc)
            preparedStatement.setInt(3, id!!)

            preparedStatement.executeUpdate()
        }
        println("Task berhasil diupdate\n---")

    } else if (newTitle?.isBlank() == true && newDesc?.isNotBlank() == true) {
        // Update only the description
        connection.prepareStatement(updateSqlCumaDeskripsi).use { preparedStatement ->
            preparedStatement.setString(1, newDesc)
            preparedStatement.setInt(2, id!!)

            preparedStatement.executeUpdate()
        }
        println("Task berhasil diupdate\n---")
    } else if (newTitle?.isNotBlank() == true && newDesc?.isBlank() == true) {
        // Update only the title
        connection.prepareStatement(updateSqlCumaTitle).use { preparedStatement ->
            preparedStatement.setString(1, newTitle)
            preparedStatement.setInt(2, id!!)

            preparedStatement.executeUpdate()
        }
        println("Task berhasil diupdate\n---")
    }












}
fun deleteFun(connection: Connection,id:Int?){
    connection.createStatement().use {
        print("Apakah anda yakin menghapus? (Y/N): ")
        val confirmInput: String = readLine()!!.toUpperCase()
        when (confirmInput) {
            "Y" -> {
                // Delete task from the database
                val deleteQuery = "DELETE FROM tasks WHERE kode_task=$id"
                it.executeUpdate(deleteQuery)
                println("Task berhasil dihapus.")
                showListTask(connection)
            }
            "N" -> {
                println("Task tidak dihapus.")
                showListTask(connection)
            }
            else -> {
                println("JAWAB Y / N BWANG ELAH DAH")
                showListTask(connection)
            }
        }
    }
}

fun isTableNotEmpty(connection: Connection): Boolean {
    // SQL query to check if the table is not empty
    val countSql = "SELECT COUNT(*) FROM tasks"

    connection.prepareStatement(countSql).use { statement ->
        val resultSet: ResultSet = statement.executeQuery()
        resultSet.next()
        val rowCount = resultSet.getInt(1)
        return rowCount > 0
    }
}
fun backMainState() {
    main()
}
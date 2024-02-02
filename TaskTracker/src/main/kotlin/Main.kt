import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

fun main() {
    // Database connection details
    val connectionUrl = "jdbc:mysql://localhost:3306/tasktracker_db"
    val user = "root"
    val password = ""

    DriverManager.getConnection(connectionUrl, user, password).use { connection ->
        if (isTableNotEmpty(connection)) {
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
                    val inputKodeTask = readLine()?.toInt()
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
        val id=it.executeQuery(getId)
        while (id.next()){
            val tittle= id.getString("judul")
            val desc=id.getString("deskripsi")
            print("----Detail task $tittle----\n")
            println("Judul:$tittle\nDeskripsi:$desc")
        }
        println("0. kembali\n1. edit\n2. hapus")
        print("masukkan pilihan anda sesuai angka untuk mengedit atau menghapus task:")
        val input:Int= readLine()!!.toInt()
        when (input) {
            0 -> showListTask(connection)
            else -> {}
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
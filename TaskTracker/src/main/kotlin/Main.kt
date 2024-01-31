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
        while (true){
            //Cek apakah tabel kosong
            if (isTableNotEmpty(connection)) {
                print("TASK TRACKER \n1. Create\n2.Lihat list task\nMasukkan pilihan anda: ")
                var input: Int? = readLine()?.toInt()
                if (input == 1){addRow(connection)}
            }else {print("TASK TRACKER \n1. Create \nTask kosong, tekan 1 untuk tambah task: ")
                var input: Int? = readLine()?.toInt()
                if (input == 1){addRow(connection)}
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
    val judul:String? = readLine()
    print("Masukkan Deskripsi: ")
    val deskripsi:String? = readLine()

    // Memasukkan input pengguna kedalam insert statement
    connection.prepareStatement(insertSql).use { preparedStatement ->
        preparedStatement.setString(1, judul)
        preparedStatement.setString(2, deskripsi)
        preparedStatement.executeUpdate()
    }
    println("Data berhasil dimasukkan\n---")
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
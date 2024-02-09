package app

import Helper
import atribut.TaskAtribut
import java.lang.Exception
import java.sql.Connection
import java.util.*

class TaskTracker() {

    //tambah task
    fun addRow(connection: Connection) {
        print("---\nCreate Task\nMasukkan Judul: ")
        val insertSql = "INSERT INTO tasks (judul, deskripsi) VALUES (?, ?)"

        val judul: String? = readLine()
        print("Masukkan Deskripsi: ")
        val deskripsi: String? = readLine()


        if (judul!!.isNotBlank() && deskripsi!!.isNotBlank()) {
            connection.prepareStatement(insertSql).use { preparedStatement ->
                preparedStatement.setString(1, judul)
                preparedStatement.setString(2, deskripsi)
                preparedStatement.executeUpdate()
            }
            println("Data berhasil dimasukkan\n---")
        } else {
            println("Judul dan Deskripsi tidak boleh kosong\n---")
//            main.backMainState()
        }
    }

    //show list task
    fun showListTask(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()
        val listQuery = "SELECT * FROM tasks"
        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    task.add(TaskAtribut(kodeTask, titleTask, desc))
                }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }

    fun detailTask(connection: Connection, id: Int, main: Main) {
        val getId = "SELECT * FROM tasks WHERE kode_task=$id"
        connection.createStatement().use {
            val resultId = it.executeQuery(getId)
            while (resultId.next()) {
                val tittle = resultId.getString("judul")
                val desc = resultId.getString("deskripsi")
                print("----Detail task $tittle----\n")
                println("Judul:$tittle\nDeskripsi:$desc")
            }

            println("0. kembali\n1. edit\n2. hapus")
            print("masukkan pilihan anda sesuai angka untuk mengedit atau menghapus task:")
            val input: Int = readLine()!!.toInt()
            when (input) {
                0 -> main.backMainState()

                1 -> {
                    // Update task
                    updateTask(connection, main.inputKodeTask)
                    main.backMainState()

                }

                2 -> {
                    deleteFun(connection, main.inputKodeTask)
                    main.backMainState()
                }

                else -> {}
            }
        }
    }

    fun deleteFun(connection: Connection, id: Int?) {
        connection.createStatement().use {
            print("Apakah anda yakin menghapus? (Y/N): ")
            val confirmInput: String = readLine()!!.uppercase(Locale.getDefault())
            when (confirmInput) {
                "Y" -> {
                    // Delete task from the database
                    val deleteQuery = "DELETE FROM tasks WHERE kode_task=$id"
                    it.executeUpdate(deleteQuery)
                    println("Task berhasil dihapus.")
                }

                "N" -> {
                    println("Task tidak dihapus.")
                }

                else -> {
                    println("JAWAB Y / N BWANG ELAH DAH")

                }
            }
        }
    }


    fun updateTask(connection: Connection, id: Int?) {

        print("Masukkan Judul Baru: ")
        val newTitle: String? = readLine()
        print("Masukkan Deskripsi Baru: ")
        val newDesc: String? = readLine()
        val updateSql = "UPDATE tasks SET judul=?, deskripsi=? WHERE kode_task=?"

        val updateSqlCumaDeskripsi = "UPDATE tasks SET   deskripsi=? WHERE kode_task=?"
        val updateSqlCumaTitle = "UPDATE tasks SET  judul=? WHERE kode_task=?"

        if (newTitle?.isNotBlank() == true && newDesc?.isNotBlank() == true) {

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
}

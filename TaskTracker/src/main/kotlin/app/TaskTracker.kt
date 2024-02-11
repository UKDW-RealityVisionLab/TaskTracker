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
        val insertSql = "INSERT INTO tasks (judul, deskripsi, tanggal,prioritas) VALUES (?, ?, ?,?)"

        val judul: String? = readLine()
        print("Masukkan Deskripsi: ")
        val deskripsi: String? = readLine()
        print("Masukkan Tanggal Jatuh Tempo(DD-MM-YYYY): ")
        val tanggal: String? = readLine()
        print("Masukkan Prioritas Task (low/medium/high): ")
        val prioritas: String? = readLine()


        if (judul!!.isNotBlank() && deskripsi!!.isNotBlank()) {
            connection.prepareStatement(insertSql).use { preparedStatement ->
                preparedStatement.setString(1, judul)
                preparedStatement.setString(2, deskripsi)
                preparedStatement.setString(3, tanggal)
                preparedStatement.setString(4, prioritas)
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
                val tgl = resultId.getString("tanggal")
                val prior = resultId.getString("prioritas")
                print("----Detail task $tittle----\n")
                println("Judul:$tittle\nDeskripsi:$desc\nTanggal Jatuh Tempo:$tgl\nPrioritas:$prior")
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

    fun search_judul(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()


        print("Masukkan Kata Kunci : ")
        val kataKunci: String? = readLine()
        val listQuery = "SELECT * FROM tasks where judul like " + "'%$kataKunci%';"




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

    fun search_kode(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()


        print("Masukkan Kata Kunci : ")
        val kataKunci: String? = readLine()
        val listQuery = "SELECT * FROM tasks where kode_task like " + "'%$kataKunci%';"




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

    fun updateTask(connection: Connection, id: Int?) {

        print("---\nUPDATE TASK (kosongkan jika tidak ingin diupdate)\nMasukkan Judul Baru: ")
        val newTitle: String? = readLine()
        print("Masukkan Deskripsi Baru: ")
        val newDesc: String? = readLine()
        print("Masukkan Tanggal Jatuh Tempo Baru(DD-MM-YYYY): ")
        val newTgl: String? = readLine()
        print("Masukkan Prioritas Baru (low, medium, high): ")
        val newPrior: String? = readLine()

        val updateSql = "UPDATE tasks SET judul=?, deskripsi=?, tanggal=?, prioritas=? WHERE kode_task=?"

        val updateSqlCumaDeskripsi = "UPDATE tasks SET   deskripsi=? WHERE kode_task=?"
        val updateSqlCumaTitle = "UPDATE tasks SET  judul=? WHERE kode_task=?"
        val updateSqlCumaTgl = "UPDATE tasks SET  tanggal=? WHERE kode_task=?"
        val updateSqlCumaPrior = "UPDATE tasks SET prioritas =? WHERE kode_task=?"

        if (newTitle?.isNotBlank() == true && newDesc?.isNotBlank() == true && newTgl?.isNotBlank() == true)  {

            connection.prepareStatement(updateSql).use { preparedStatement ->
                preparedStatement.setString(1, newTitle)
                preparedStatement.setString(2, newDesc)
                preparedStatement.setString(3, newTgl)
                preparedStatement.setString(4,newPrior)
                preparedStatement.setInt(5, id!!)

                preparedStatement.executeUpdate()
            }
            println("Task berhasil diupdate\n---")

        } else if (newTitle?.isBlank() == true && newDesc?.isNotBlank() == true && newTgl?.isNotBlank() == true) {
            // Update only the description
            connection.prepareStatement(updateSqlCumaDeskripsi).use { preparedStatement ->
                preparedStatement.setString(1, newDesc)
                preparedStatement.setInt(2, id!!)

                preparedStatement.executeUpdate()
            }
            println("Task berhasil diupdate\n---")
        } else if (newTitle?.isNotBlank() == true && newDesc?.isBlank() == true && newTgl?.isNotBlank() == true) {
            // Update only the title
            connection.prepareStatement(updateSqlCumaTitle).use { preparedStatement ->
                preparedStatement.setString(1, newTitle)
                preparedStatement.setInt(2, id!!)

                preparedStatement.executeUpdate()
            }
            println("Task berhasil diupdate\n---")
        } else if (newTitle?.isNotBlank() == true && newTgl?.isBlank() == true && newDesc?.isNotBlank() == true) {
            // Update only the tanggal
            connection.prepareStatement(updateSqlCumaTgl).use { preparedStatement ->
                preparedStatement.setString(1, newTgl)
                preparedStatement.setInt(2, id!!)

                preparedStatement.executeUpdate()
            }
            println("Task berhasil diupdate\n---")

        } else if (newTitle?.isBlank() == true && newTgl?.isBlank() == true && newDesc?.isBlank() == true && newPrior?.isNotBlank()==true) {
            // hanya update prioritas
            connection.prepareStatement(updateSqlCumaPrior).use { preparedStatement ->
                preparedStatement.setString(1, newPrior)
                preparedStatement.setInt(2, id!!)

                preparedStatement.executeUpdate()
            }
            println("Task berhasil diupdate\n---")

        }
    }
}

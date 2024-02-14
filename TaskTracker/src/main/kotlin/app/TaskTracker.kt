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
        val insertSql = "INSERT INTO tasks (judul, deskripsi, tanggal,prioritas,status) VALUES (?, ?, ?,?,?)"

        val judul: String? = readLine()
        print("Masukkan Deskripsi: ")
        val deskripsi: String? = readLine()
        print("Masukkan Tanggal Jatuh Tempo(DD-MM-YYYY): ")
        val tanggal: String? = readLine()
        var prioritas: String?
        do {
            print("Masukkan Prioritas Task (low/medium/high): ")
            prioritas = readLine()?.lowercase()
            if (prioritas !in listOf("low", "medium", "high")) {
                println("Hanya dapat memasukkan input low / medium / high saja")
            }
        } while (prioritas !in listOf("low", "medium", "high"))

        var status: String?
        do {
            print("Masukkan Status Task (TODO/IN PROGRESS/DONE): ")
            status = readLine()?.uppercase()
            if (status !in listOf("TODO" ,"IN PROGRESS" ,"DONE"))  {
                println("Hanya dapat memasukkan input TODO/IN PROGRESS/DONE")
            }
        } while (status !in listOf("TODO" ,"IN PROGRESS" ,"DONE"))


        print("Apakah anda yakin menambahkan?\n Judul : $judul\n Deskripsi : $deskripsi\n Tanggal : $tanggal\n Prioritas : $prioritas\n Status: $status\n (Y/N): ")
        val confirmAdd: String = readLine()!!.uppercase(Locale.getDefault())
        when (confirmAdd) {
            "Y"->{if (judul!!.isNotBlank() && deskripsi!!.isNotBlank()) {
                connection.prepareStatement(insertSql).use { preparedStatement ->
                    preparedStatement.setString(1, judul)
                    preparedStatement.setString(2, deskripsi)
                    preparedStatement.setString(3, tanggal)
                    preparedStatement.setString(4, prioritas)
                    preparedStatement.setString(5,status)
                    preparedStatement.executeUpdate()
                }
                println("Data berhasil dimasukkan\n---")
            } else {
                println("Judul dan Deskripsi dan Tanggal dan Prioritas tidak boleh kosong\n---")
//            main.backMainState()
            }
            }
            "N"->{
                println("Data tidak berhasil dimasukkan\n---")
            }else -> {
            println("JAWAB HANYA Y ATAU N SAJA")
        }
        }
    }

    //show list task
    fun showListTask(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()
        val listQuery = "SELECT * FROM tasks"


        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc, status, prio, status))
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
                val status = resultId.getString("status")
                print("----Detail task $tittle----\n")
                println("Judul:$tittle\nDeskripsi:$desc\nTanggal Jatuh Tempo:$tgl\nPrioritas:$prior\nStatus: $status")
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

        print("---\nUPDATE TASK (kosongkan jika tidak ingin diupdate)\nMasukkan Judul Baru: ")
        val newTitle: String? = readLine()
        print("Masukkan Deskripsi Baru: ")
        val newDesc: String? = readLine()
        print("Masukkan Tanggal Jatuh Tempo Baru(DD-MM-YYYY): ")
        val newTgl: String? = readLine()
        var newPrioritas: String?
        do {
            print("Masukkan Prioritas Task (low/medium/high): ")
            newPrioritas = readLine()?.lowercase()
            if (newPrioritas !in listOf("low", "medium", "high")) {
                println("Hanya dapat memasukkan input low / medium / high")
            }
        } while (newPrioritas !in listOf("low", "medium", "high"))

        var newStatus: String?
        do {
            print("Masukkan Status Task (TODO/IN PROGRESS/DONE): ")
            newStatus = readLine()?.uppercase()
            if (newStatus !in listOf("TODO", "IN PROGRESS", "DONE")) {
                println("Hanya dapat memasukkan input TODO/IN PROGRESS/DONE")
            }
        } while (newStatus !in listOf("TODO", "IN PROGRESS", "DONE"))

        val updateSql = "UPDATE tasks SET judul=?, deskripsi=?, tanggal=?, prioritas=? WHERE kode_task=?"

        val updateSqlCumaDeskripsi = "UPDATE tasks SET   deskripsi=? WHERE kode_task=?"
        val updateSqlCumaTitle = "UPDATE tasks SET  judul=? WHERE kode_task=?"
        val updateSqlCumaTgl = "UPDATE tasks SET  tanggal=? WHERE kode_task=?"
        val updateSqlCumaPrior = "UPDATE tasks SET prioritas =? WHERE kode_task=?"
        val updateSqlCumastatus = "UPDATE tasks SET status =? WHERE kode_task=?"

        print("Apakah anda yakin mengubah data? (Y/N): ")
        val confirmUpdate: String = readLine()!!.uppercase(Locale.getDefault())
        when (confirmUpdate) {
            "Y" -> {
                if (newTitle?.isNotBlank() == true && newDesc?.isNotBlank() == true && newTgl?.isNotBlank() == true) {

                    connection.prepareStatement(updateSql).use { preparedStatement ->
                        preparedStatement.setString(1, newTitle)
                        preparedStatement.setString(2, newDesc)
                        preparedStatement.setString(3, newTgl)
                        preparedStatement.setString(4, newPrioritas)
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

                } else if (newTitle?.isBlank() == true && newTgl?.isBlank() == true && newDesc?.isBlank() == true && newPrioritas?.isNotBlank() == true) {
                    // hanya update prioritas
                    connection.prepareStatement(updateSqlCumaPrior).use { preparedStatement ->
                        preparedStatement.setString(1, newPrioritas)
                        preparedStatement.setInt(2, id!!)

                        preparedStatement.executeUpdate()
                    }
                    println("Task berhasil diupdate\n---")

                } else if (newStatus?.isNotBlank() == true && newTitle?.isBlank() == true && newTgl?.isBlank() == true && newDesc?.isBlank() == true && newPrioritas?.isBlank() == true) {
                    // hanya update status
                    connection.prepareStatement(updateSqlCumastatus).use { preparedStatement ->
                        preparedStatement.setString(1, newStatus)
                        preparedStatement.setInt(2, id!!)

                        preparedStatement.executeUpdate()
                    }
                    println("Task berhasil diupdate\n---")

                }
            }

            "N" -> {
                println("Data tidak berhasil diupdate\n---")
            }

            else -> {
                println("JAWAB HANYA Y ATAU N SAJA")
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
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))
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
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))                }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }


    fun search_status(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()


        print("Masukkan Kata Kunci : ")
        val kataKunci: String? = readLine()
        val listQuery = "SELECT * FROM tasks where status like " + "'%$kataKunci%';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))                }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }







    fun search_prioritasLow(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()



        val listQuery = "SELECT * FROM tasks where prioritas = " + "'low';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))
                }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }





    fun search_prioritasMedium(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()



        val listQuery = "SELECT * FROM tasks where prioritas = " + "'medium';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))
                }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }


    fun search_prioritasHigh(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()



        val listQuery = "SELECT * FROM tasks where prioritas = " + "'high';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))                 }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }




    fun search_statusTODO(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()



        val listQuery = "SELECT * FROM tasks where status = " + "'TODO';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))                 }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }






    fun search_statusIN_PROGRESS(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()



        val listQuery = "SELECT * FROM tasks where status = " + "'IN PROGRESS';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))                 }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }






    fun search_statusDONE(connection: Connection): Helper<MutableList<TaskAtribut>> {
        val task = mutableListOf<TaskAtribut>()



        val listQuery = "SELECT * FROM tasks where status = " + "'DONE';"




        print("--\nList task anda:\n")

        return try {
            connection.createStatement().use {
                val fetchList = it.executeQuery(listQuery)
                while (fetchList.next()) {
                    val kodeTask = fetchList.getInt("kode_task")
                    val titleTask = fetchList.getString("judul")
                    val desc = fetchList.getString("deskripsi")
                    val tgl=  fetchList.getString("tanggal")
                    val prio=  fetchList.getString("prioritas")
                    val status=  fetchList.getString("status")
                    task.add(TaskAtribut(kodeTask, titleTask, desc,tgl,prio,status))                 }
                Helper.Success(task)
            }
        } catch (e: Exception) {
            Helper.Failed("fail to get task: ${e.message}")
        }
    }





}

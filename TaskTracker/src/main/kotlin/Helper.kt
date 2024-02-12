sealed class Helper<out R> private constructor() {
    data class Success<out T>(
        val data: T
    ) : Helper<T>()

    data class Failed(val errorMessage: String, val data: Nothing? = null) : Helper<Nothing>()
}
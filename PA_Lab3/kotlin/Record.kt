private var idCount = 1

class Record(var key: Int = idCount, var data: String = getRandomString(10)): java.io.Serializable{
    init {
        idCount++
    }

    override fun toString(): String{
        return "$key:$data"
    }
}

private fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}



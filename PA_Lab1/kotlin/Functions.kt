import java.io.File
import kotlin.random.Random

/**
 * Generates file with random integer numbers. File size is inputted by user
 */
fun generateFile(name: String) {

    var (text, size) = enterFileSize()
    var margins = 1

    when(text){
        "GB" -> {
            margins = 10000000
            size *= 1024 * 1024 * 1024
            println("File with name $name and size ${generateNumbers(name, margins, size)/(1024*1024*1024)} in GB was generated")
        }
        "MB" -> {
            margins = 100000
            size *= 1024 * 1024
            println("File with name $name and size ${generateNumbers(name, margins, size)/(1024*1024)} in MB was generated")
        }
        "KB" -> {
            margins = 100
            size *= 1024
            println("File with name $name and size ${generateNumbers(name, margins, size)/(1024)} in KB was generated")
        }
        "B" -> {
            margins = 1
            println("File with name $name and size ${generateNumbers(name, margins, size)} in B was generated")
        }
        else -> {
            assert(false)
        }
    }
}

/**
 * Lets user choose between different size measurements (gb/mb/kb/b) and the size itself
 */
private fun enterFileSize(): Pair<String, Long>{
    var text: String
    do {
        print("Do you want enter file size in GB or MB? (gb/mb/kb/b): ")
        text = readLine()!!.uppercase()
    } while (text != "GB" && text != "MB" && text != "KB" && text != "B")

    val size= enterInt(1,"Enter size of the file in $text: ")
    return Pair(text, size)
}

/**
 * Asks user to input an integer, which should be greater or equal to parameter upperBound
 */
fun enterInt(upperBound: Int, header: String): Long{
    var size = 0L
    var flag = true

    while (flag) {
        try {
            print(header)
            size = readLine()!!.toLong()

            if (size >= upperBound) {
                flag = false
            }

        } catch (_: Exception) {}

    }
    return size
}

/**
 * Asks user to input an boolean using yes or no keywords
 */
fun enterBoolean(header: String): Boolean{
    var text: String
    do {
        print(header)
        text = readLine()!!.uppercase()
    } while (text != "YES" && text != "NO")
    return text == "YES"
}

/**
 * Generates random integers and writes them in the file, until the chosen size of the file is reached
 */
private fun generateNumbers(fileName: String, margins: Int, size: Long): Long{
    val file = File(fileName)
    file.createNewFile()
    file.writeText("")
    val rand = Random(System.currentTimeMillis())

    var counter = margins

    file.appendText(List(margins) { rand.nextInt().toString() }.joinToString("\n"))
    while (file.length() < size) {
        file.appendText(List(margins) {rand.nextInt().toString() }.joinToString("\n", "\n"))
        counter += margins
    }

    println("Number of generated elements: $counter")

    return file.length()
}


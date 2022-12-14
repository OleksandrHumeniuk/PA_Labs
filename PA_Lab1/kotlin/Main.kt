
fun main() {
    val inputFileName = "input.txt"
    val outputFileName = "output.txt"
    generateFile(inputFileName)

    val m = enterInt(2, "Enter number m: ").toInt()
    val useNew = enterBoolean("Do you want to use new algorithm (yes/no): ")

    val startTime = System.currentTimeMillis()
    if (useNew) {
        newMultiwayMergeSort(inputFileName, outputFileName, m)
    }
    else{
        oldMultiwayMergeSort(inputFileName, outputFileName, m)
    }

    val endTime = System.currentTimeMillis()
    val seconds = (endTime - startTime) / 1000.0
    println("Running time of the program with m=$m is ${seconds.toInt() / 60} minutes and ${(seconds % 60).toInt()} seconds")
}





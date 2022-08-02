import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Unoptimized program implementation of multiway merge sort
 */
fun oldMultiwayMergeSort(inputFileName: String, outputFileName: String, m: Int) {

    val fileA = arrayOf(File(inputFileName))  //Array with file A
    val filesB = Array(m) { i -> File("B${i + 1}.txt").also { it.delete(); it.createNewFile() } } //Array with assisting files B
    val filesC = Array(m) { i -> File("C${i + 1}.txt").also { it.delete(); it.createNewFile() } } //Array with assisting files C

    multiwayMerge(fileA, filesB) //Before the main loop starts, all elements are transfered from file A to files B

    var flag = 1
    while (!isSorted(fileA[0], filesB[0], filesC[0])) {  // Main loop of the function. Elements are merging from files B
        if (flag == 1) {                                 // to files C and back until B1 or C1 is fully sorted
            multiwayMerge(filesB, filesC)
        } else {
            multiwayMerge(filesC, filesB)
        }
        flag = -flag
    }

    val outputFile = File(outputFileName)  //Destination file
    outputFile.createNewFile()

    if (filesB[0].length() == fileA[0].length()) {
        filesB[0].copyTo(outputFile, true)
    } else {
        filesC[0].copyTo(outputFile, true)
    }
}

/**
 * Checks whether sorting is fully done by comparing length of files B1 and C1 to A
 */
private fun isSorted(arrA: File, arrB: File, arrC: File): Boolean {
    return arrB.length() == arrA.length() || arrC.length() == arrA.length()
}

/**
 * Merges elements from inputFiles to outputFiles
 */
private fun multiwayMerge(inputFiles: Array<File>, outputFiles: Array<File>) {

    val bufferedReaders = Array(inputFiles.size) { i -> BufferedReader(FileReader(inputFiles[i])) } // Array with readers of inputFiles
    outputFiles.forEach { it.writeText("")} // Deletes all data from outputFiles before writing to them

    var j = 0    // Keeps track in which outputFile to write
    val set = ArrayList<Int>(0) // Current set of sorted elements

    while (!isMerged(bufferedReaders)) { //Merges files until they are fully merged
        var minValue = Int.MAX_VALUE
        var minIndex: Int? = null

        for (i in bufferedReaders.indices) { //Find minimum among current elements of all inputFiles

            val text = peek(bufferedReaders[i])

            if (text != null) {             // Checks whether OEF is reached
                val num = text.toInt()

                if (set.isEmpty() || num >= set.last()) { // Checks whether set is empty or current element is greater than
                                                          // it's last element

                    if (num <= minValue) {                // Checks whether current element is smaller than minValue

                        minValue = num        // If all conditions are satisfied: current elements is the new min element
                        minIndex = i
                    }
                }
            }
        }

        if (minIndex == null) {                  // If minimum element wasn't found - writes set to the according file
            if (outputFiles[j].length() > 0L) {  // and empty set
                outputFiles[j].appendText("\n")
            }
            outputFiles[j].appendText(set.joinToString("\n"))

            set.clear()
            j = (j + 1) % outputFiles.size

        } else {                               // If minimum element was found - just adds it to the set and increase
            set.add(minValue)                  // bufferedReader (file pointer)
            bufferedReaders[minIndex].readLine()
        }
    }

    if (outputFiles[j].length() > 0L) {         // After main loop ends, program still has to write the last set left
        outputFiles[j].appendText("\n")
    }
    outputFiles[j].appendText(set.joinToString("\n"))

    bufferedReaders.forEach { it.close() }
}

/**
 * Checks whether all elements from inputFiles have been merged to outputFiles
 */
private fun isMerged(readers: Array<BufferedReader>): Boolean {
    for (reader in readers) {
        val s = peek(reader)
        if (s != null) {
            return false
        }
    }
    return true
}

/**
 * Gets the next value (line) from bufferedReader and returns back to its original position
 */
private fun peek(reader: BufferedReader): String? {
    reader.mark(100)
    val line = reader.readLine()
    reader.reset()
    return line
}
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.math.min

/**
 * Optimized program implementation of multiway merge sort
 */
fun newMultiwayMergeSort(inputFileName: String, outputFileName: String, m: Int = 5) {
    val fileA = File(inputFileName) //File A
    val filesB = Array(m) { i -> File("B${i + 1}.txt").also { it.delete(); it.createNewFile() } } //Array with assisting files B
    val filesC = Array(m) { i -> File("C${i + 1}.txt").also { it.delete(); it.createNewFile() } } //Array with assisting files C

    initialSorting(fileA, filesB) //Before the main loop starts, chunks of file A (about 250 MB) are sorted and transfered to files B

    var flag = 1

    while (!isSorted(fileA, filesB[0], filesC[0])) {   // Main loop of the function. Elements are merging from files
        when (flag){                                   // to files C and back until B1 or C1 is fully sorted
            1    -> multiwayMerge(filesB, filesC)
            else -> multiwayMerge(filesC, filesB)
        }
        flag = -flag
    }

    val outputFile = File(outputFileName) //Destination file
    outputFile.createNewFile()

    if (filesB[0].length() == fileA.length()) {
        filesB[0].copyTo(outputFile, true)
    } else {
        filesC[0].copyTo(outputFile, true)
    }
}

/**
 * Performs initial soring of 250 MB chunks of file A
 */
private fun initialSorting(inputFile: File, outputFiles: Array<File>){
    val br = BufferedReader(FileReader(inputFile), BUFFER_SIZE)
    val bufferedWritersB = Array(outputFiles.size) {i -> BufferedWriter(FileWriter(outputFiles[i]), BUFFER_SIZE)}

    var i = 0
    while (true) {
        val charArr = readChunk(br, (CHUNK_SIZE/2))

        if (charArr.isEmpty()){
            break
        }

        val intArr = charToIntArray(charArr)
        quickSort(intArr, 0, intArr.size - 1)

        writeToFile(bufferedWritersB[i], outputFiles[i], intArr.joinToString("\n"))

        i = (i + 1) % outputFiles.size
    }

    br.close()
    bufferedWritersB.forEach { it.close() }
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

    val bufferedWriters = Array(outputFiles.size) { i -> BufferedWriter(FileWriter(outputFiles[i]), BUFFER_SIZE) } // Array of bufferedReaders of outputFiles
    val bufferedReaders = Array(inputFiles.size) { i -> BufferedReader(FileReader(inputFiles[i]), BUFFER_SIZE) } //  Array of bufferedWriters of inputFiles

    val outputFilesEmpty = Array(outputFiles.size){true} // Array that keeps track of, which outputFiles are still empty
    val bufferedArrays = ArrayList<IntArray>(0) // 2D array that keep current chunks of all inputFiles
    val pointerArr = IntArray(inputFiles.size){-1}   // Array that keep positions of current elements of bufferedArrays.
                                                     // -1 means that the end of array is reached
    val chunkSize = CHUNK_SIZE / inputFiles.size

    bufferedReaders.withIndex().forEach{  // Reads first chunks of all non-empty inputFiles
        if (peek(it.value) != null){
            bufferedArrays.add(charToIntArray(readChunk(bufferedReaders[it.index], chunkSize)))
            pointerArr[it.index] = 0
        }
    }

    var j = 0 // Keeps track in which outputFile to write
    val set = ArrayList<Int>(0) // Current set of sorted elements

    while (!isMerged(pointerArr)) { //Merges files until they are fully merged

        val minIndex = findMin(bufferedArrays, pointerArr, set) //Find minimum among current elements of all inputFiles

        if (minIndex == null || set.size >= chunkSize/4) { // If minimum element wasn't found or current set is
                                                            //too big - writes set to the according file
            writeToFile(bufferedWriters[j], !outputFilesEmpty[j], set.joinToString("\n"))
            outputFilesEmpty[j] = false
            set.clear()
            if (minIndex == null) j = (j + 1) % outputFiles.size

        }

        if (minIndex != null) { // If minimum element was found - just adds it to the set and increase according pointers

            set.add(bufferedArrays[minIndex][pointerArr[minIndex]])

            pointerArr[minIndex] ++
            if (pointerArr[minIndex] >= bufferedArrays[minIndex].size){ // If the end of oen of the arrays is reached
                                                                        // reads new chunk and resets the pointer
                val s = peek(bufferedReaders[minIndex])
                if (s != null) {
                    bufferedArrays[minIndex] = charToIntArray(readChunk(bufferedReaders[minIndex], chunkSize))
                    pointerArr[minIndex] = 0
                }
                else{
                    pointerArr[minIndex] = -1
                    bufferedArrays[minIndex] = IntArray(0)
                }
            }
        }
    }

    writeToFile(bufferedWriters[j], !outputFilesEmpty[j], set.joinToString("\n")) // After main loop ends, program still has to write the last set left

    bufferedReaders.forEach { it.close() }
    bufferedWriters.forEach { it.close() }
}

/**
 * Writes text in the file and places a "\n" before it, if needed
 */
private fun writeToFile(writer: BufferedWriter, file: File, text: String){
    if (file.length() > 0L) {
        writer.write("\n")
    }
    writer.write(text)
}

/**
 * Writes text in the file and places a "\n" before it, if needed
 */
private fun writeToFile(writer: BufferedWriter, isEmpty: Boolean, text: String){
    if (isEmpty) {
        writer.write("\n")
    }
    writer.write(text)
}

/**
 * Finds minimum element that satisfy all conditions
 */
private fun findMin(bufferedArrays: ArrayList<IntArray>, pointerArr: IntArray, set: ArrayList<Int>): Int?{
    var minValue = Int.MAX_VALUE
    var minIndex: Int? = null

    for (i in bufferedArrays.indices) {

        if (pointerArr[i] != -1) {   // Checks whether end of array is reached

            val num = bufferedArrays[i][pointerArr[i]]

            if (set.isEmpty() || num >= set.last()) { // Checks whether set is empty or current element is greater than
                                                      // it's last element
                if (num <= minValue) {               // Checks whether current element is smaller than minValue

                    minValue = num        // If all conditions are satisfied: current elements is the new min element
                    minIndex = i
                }
            }
        }
    }
    return minIndex
}

/**
 * Checks whether all elements from inputFiles have been merged to outputFiles
 */
private fun isMerged(pointerArrs: IntArray): Boolean {
    for (pointer in pointerArrs) {
        if (pointer != -1) {
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

/**
 * Converts charArray to IntArray
 */
private fun charToIntArray(charArray: CharArray): IntArray {
    return charArray.joinToString("").split("\n").map{it.toInt()}.toIntArray()
}

/**
 * Reads and returns chunk of file with specified size
 */
private fun readChunk(br: BufferedReader, chunkSize: Int): CharArray{
    var charArr = CharArray(chunkSize)
    br.read(charArr)

    if (charArr.last().code != 0){
        val arr = ArrayList<Char>()
        while(true){
            val char = br.read().toChar()
            if (char == '\n' || char.code == 0){
                charArr += arr
                break
            }
            else{
                arr.add(char)
            }
        }
    }
    else{
        charArr = charArr.filter{it.code != 0}.toCharArray()
    }

    return charArr
}


import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun saveTree(tree: Tree){
    try {
        val fileOut = FileOutputStream(SAVE_FILE_PATH)
        val objectOut = ObjectOutputStream(fileOut)
        objectOut.writeObject(tree)
        objectOut.close()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun loadTree(): Tree?{
    return try {
        val fileIn = FileInputStream(SAVE_FILE_PATH)
        val objectIn = ObjectInputStream(fileIn)
        val obj = objectIn.readObject()
        println("The Object has been read from the file")
        objectIn.close()
        obj as Tree
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
        null
    }
}


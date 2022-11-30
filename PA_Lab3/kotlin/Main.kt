

fun main(){
    println("editkey/editrecord/insert/delete/search")
    var tree = Tree()

    for (i in 0..50) {
        tree.insert(Record())
        tree.printTree()
    }
//    tree = loadTree()
//    tree!!.printTree()
    while(true){
        println()
        val input = readLine()!!
        if (input == "insert"){
            tree.insert(Record())
        }
        else if (input.contains("delete")){
            val result = tree.delete(input.split(" ").last().toInt())
            println("Deletion status: $result")
        }
        else if (input.contains("search")){
            val result = tree.search(input.split(" ").last().toInt())
            if (result == null){
                println("Record not found!")
            }
            else{
                println("Found record: $result")
            }
        }
        else if (input.contains("editkey")){
            tree.editRecordKey(input.split(" ")[1].toInt(), input.split(" ")[2].toInt())
        }
        else if (input.contains("editrecord")){
            tree.editRecordData(input.split(" ")[1].toInt(), input.split(" ")[2])
        }
        else if (input == "exit"){
            println("Ending program");
            saveTree(tree)
            break
        }
        else{
            println("Didn't understand you. Try again!")
        }
        tree.printTree()
    }

}

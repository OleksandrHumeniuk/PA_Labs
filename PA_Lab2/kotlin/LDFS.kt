import java.lang.Integer.max

fun findSolutionLDFS(initialBoard: Board): Board?{
    return recursiveLDFS(initialBoard)
}


var totalStateCounter = 0
var memoryStateCounter = 1
var maxMemoryStateCounter = memoryStateCounter
var deadEndCounter = 0

fun recursiveLDFS(board: Board): Board?{
    memoryStateCounter -= 1
    maxMemoryStateCounter = max(maxMemoryStateCounter, memoryStateCounter)
    if (board.conflictNum == 0) return board
    else if (board.depth == MAX_DEPTH) {
        deadEndCounter++
        return null
    }
    else{
        val children = board.generateChildren()
        memoryStateCounter += children.size
        totalStateCounter += children.size
        for (child in children){
            val result = recursiveLDFS(child)
            if (result != null) return result
        }
    }
    return null
}



import java.lang.Integer.max
import java.util.*

class BoardOrderedArray {
    var values = ArrayList<Board>(0)

    fun insert(board: Board) {
        values.add(board)
        values.sortBy {it.value}
    }

    fun append(arr: ArrayList<Board>){
        values += arr
        values.sortBy {it.value}
    }

    fun isNotEmpty(): Boolean{
        return values.isNotEmpty()
    }

    fun extractMin(): Board{
        return values.removeFirst()
    }

    fun isPresent(key: Board): Boolean {
        val keyVal = key.value
        var low = 0
        var high = values.size - 1
        var startIndex = -1
        while (low <= high) {
            val mid = (high - low) / 2 + low
            val midVal = values[mid].value
            if (midVal > keyVal) {
                high = mid - 1
            } else if (midVal == keyVal) {
                startIndex = mid
                high = mid - 1
            } else {
                low = mid + 1
            }
        }
        if (startIndex != -1) {
            var i = 0
            var elem: Board
            do {
                elem = values[startIndex + i]
                if (elem == key) {
                    return true
                }
                i++
            } while (startIndex + i < values.size && values[startIndex + i].conflictNum == key.conflictNum)
        }
        return false
    }
}

var totalStateCounter2 = 0
var maxMemoryStateCounter2 = 0
var deadEndCounter2 = 0

fun findSolutionAStar(initialBoard: Board): Board?{
    val open = BoardOrderedArray()
    val closed = BoardOrderedArray()
    var current: Board

    open.insert(initialBoard)
    while (open.isNotEmpty()){
        current = open.extractMin()
        maxMemoryStateCounter2 = max(open.values.size, totalStateCounter2)
        if (current.conflictNum == 0) {
            maxMemoryStateCounter2 += closed.values.size
            totalStateCounter2 = closed.values.size + open.values.size
            return current
        }
        closed.insert(current)
        val children = ArrayList(current.generateChildren().filter {!closed.isPresent(it)})
        if (children.size == 0) deadEndCounter2++
        open.append(children)
    }
    return null
}

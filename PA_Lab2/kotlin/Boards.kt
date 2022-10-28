
interface Board{
    var conflictNum: Int
    var placements: ArrayList<Pair<Int, Int>>
    var depth: Int
    var parent: Board?
    var value: Int
    fun generateChildren(): ArrayList<Board>

    fun calculateConflictNum() {
        val xValues = placements.map { it.first }
        val yValues = placements.map { it.second }
        conflictNum = getDuplicatesNum(xValues) + getDuplicatesNum(yValues)

        val directionalConflicts = ArrayList<Pair<Pair<Int, Int>, Pair<Int, Int>>>(0)

        for (i in 0 until QUEEN_NUM) {
            checkDirection(i, 1, 1, directionalConflicts)
            checkDirection(i, -1, 1, directionalConflicts)
            checkDirection(i, 1, -1, directionalConflicts)
            checkDirection(i, -1, -1, directionalConflicts)
        }
        conflictNum += directionalConflicts.size
    }

    private fun getDuplicatesNum(arr: List<Int>): Int {
        return arr.size - arr.distinct().count();
    }

    private fun checkDirection(queenNum: Int, xIncrement: Int, yIncrement: Int, conflicts: ArrayList<Pair<Pair<Int, Int>, Pair<Int, Int>>>){
        val (x, y) = placements[queenNum]
        for (i in 1 until QUEEN_NUM){
            val coords = Pair(x + xIncrement*i, y + yIncrement*i)
            if (placements.contains(coords)){
                addIfNotMatch(placements[queenNum], coords, conflicts)
                break
            }
        }
    }

    private fun addIfNotMatch(key1: Pair<Int,Int>, key2: Pair<Int,Int>, arr: ArrayList<Pair<Pair<Int, Int>, Pair<Int, Int>>>){
        var isMatch = false
        for (elem in arr){
            if ((elem.first == key1 && elem.second == key2) ||
                (elem.first == key2 && elem.second == key1)){
                isMatch = true
                break
            }
        }
        if (!isMatch) arr.add(Pair(key1, key2))
    }

}

fun printFinalSteps(board: Board?){
    if (board != null){
        printFinalSteps(board.parent)
        println("Step #${board.depth}")
        if (board.parent != null){
            for (i in 0 until QUEEN_NUM){
                if (board.placements[i] != board.parent!!.placements[i]) {
                    drawBoard(board.placements, i+1)
                    break
                }
            }
        }
        else{
            drawBoard(board.placements)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class EasyBoard(override var placements: ArrayList<Pair<Int, Int>>,
                override var depth: Int = 0,
                override var parent: Board? = null): Board {

    override var conflictNum = 0
    override var value: Int = 0
    init {
        if (placements.size != QUEEN_NUM) {
            throw error("Incorrect number of queens for the board class! Need 8!")
        }
        calculateConflictNum()
        value = conflictNum + depth
    }

    override fun generateChildren(): ArrayList<Board>{
        val children = ArrayList<Board>(0)
        for (i in 0 until QUEEN_NUM){
            val (x, y) = placements[i]
            for (j in 1..QUEEN_NUM){
                if (j != y){
                    val childPlacement = placements.clone() as ArrayList<Pair<Int, Int>>
                    childPlacement[i] = Pair(x, j)
                    val child = EasyBoard(childPlacement, depth + 1, this)
                    children.add(child)
                }
            }
        }
        return children
    }
}

@Suppress("UNCHECKED_CAST")
class HardBoard(override var placements: ArrayList<Pair<Int, Int>>,
                override var depth: Int = 0,
                override var parent: Board? = null): Board {

    override var conflictNum = 0
    override var value: Int = 0

    init {
        if (placements.size != QUEEN_NUM) {
            throw error("Incorrect number of queens for the board class! Need 8!")
        }
        calculateConflictNum()
        value = conflictNum + depth
    }

    override fun generateChildren(): ArrayList<Board>{
        val children = ArrayList<Board>(0)
        for (i in 0 until QUEEN_NUM) {
            generateDirectionalChildren(i, 1, 1, children)
            generateDirectionalChildren(i, -1, 1, children)
            generateDirectionalChildren(i, 1, -1, children)
            generateDirectionalChildren(i, -1, -1, children)

            generateDirectionalChildren(i, 0, 1, children)
            generateDirectionalChildren(i, 1, 0, children)
            generateDirectionalChildren(i, -1, 0, children)
            generateDirectionalChildren(i, 0, -1, children)
        }
        return children
    }

    private fun generateDirectionalChildren(queenNum: Int, xIncrement: Int, yIncrement: Int, children: ArrayList<Board>){
        val (x, y) = placements[queenNum]
        for (i in 1 until QUEEN_NUM){
            val coords = Pair(x + xIncrement*i, y + yIncrement*i)
            if (coords.first in 1..QUEEN_NUM && coords.second in 1..QUEEN_NUM && (this.parent == null || this.parent!!.placements[queenNum] != coords)){
                if (placements.contains(coords)) return
                else {
                    val childPlacement = placements.clone() as ArrayList<Pair<Int, Int>>
                    childPlacement[queenNum] = coords
                    val child = HardBoard(childPlacement, depth + 1, this)
                    children.add(child)
                }
            }
        }
    }
}
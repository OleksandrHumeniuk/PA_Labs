fun drawBoard(inputBoard: ArrayList<Pair<Int, Int>>? = null, movedQueen: Int = -1){
    val board = Array(QUEEN_NUM) { Array(QUEEN_NUM) { false } }
    if (inputBoard != null){
        for (pair in inputBoard){
            board[pair.first-1][pair.second-1] = true
        }
    }
    print("   ")
    for (i in 1..QUEEN_NUM) if (i <= 10) print(" $i ") else print("$i ")
    println()

    for (i in 1..QUEEN_NUM){
        val spacing = if (i >= 10) " " else "  "
        print("$i$spacing")
        for (j in 1..QUEEN_NUM){

            val color = if (i == movedQueen) "\u001b[34m"
                        else                 "\u001B[31m"
            val reset = "\u001b[0m" // Change to conflicting colors
            val char = if (board[i-1][j-1]) ("$color#$reset") else "_"
            print("|$char|")
        }
        println()
    }
}

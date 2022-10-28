fun main(){
    println("You may enter queens coordinates one by one or multiple at once, seperated by commas (ex. 1 2, 4 5 or 1, 2), or random")

    val inputManager = InputManager()

    while (!inputManager.isPlacementCompleted()) {
        inputManager.enterCoordinates()
        drawBoard(inputManager.placements)
    }

    println("Placement input is finished!")

    val isLDFS = inputManager.inputIsLDFS()

    val startTime = System.currentTimeMillis()
    val result = if (isLDFS) findSolutionLDFS(inputManager.getBoard())
                 else                            findSolutionAStar(inputManager.getBoard())

    val endTime = System.currentTimeMillis()
    val seconds = (endTime - startTime) / 1000.0
    println("Running time of the program is $seconds seconds")

    if (result != null){
        println("Success!")
        if (isLDFS){
            println("Total number of states: $totalStateCounter.")
            println("Max number of states in memory: $maxMemoryStateCounter.")
            println("Number of dead ends: $deadEndCounter.")
        }
        else{
            println("Total number of states: $totalStateCounter2.")
            println("Max number of states in memory: $maxMemoryStateCounter2.")
            println("Number of dead ends: $deadEndCounter2.")

        }

        drawBoard(result.placements)
        println("Found solution in ${result.depth} steps! Steps:")
        printFinalSteps(result)
    }
    else{
        println("Failure!")
    }
}
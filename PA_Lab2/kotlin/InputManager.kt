import kotlin.random.Random

class InputManager{

    var placements = ArrayList<Pair<Int, Int>>(0)
    var isHard = false

    init{
        var text: String
        do {
            print("Enter problem difficulty (easy/hard): ")
            text = readLine()!!.uppercase()
        } while (text != "EASY" && text != "HARD")
        isHard = text == "HARD"
    }

    fun getBoard(): Board{
        return if (isHard) HardBoard(placements) else EasyBoard(placements)
    }

    fun enterCoordinates(){
        print("Enter queen(s) coordinates (1-8) or random: ")
        val input = readLine()
        var isFailure = false

        if (input != null){
            if (input == "random"){
                randomGeneration()
                isFailure = false
            }
            else if (input.contains(',')){
                for (elem in input.split(", ")){
                    if (!enterCoordinate(elem)){
                        isFailure = true
                        break
                    }
                }
            }
            else{
                if (!enterCoordinate(input)){
                    isFailure = true
                }
            }
        }
        else {
            isFailure = true
        }
        println(placements)
        if (isFailure) println("Error! Some or all of your input was ignored")
    }

    fun isPlacementCompleted(): Boolean {
        return placements.size == QUEEN_NUM
    }

    private fun enterCoordinate(strCoords: String): Boolean{
        return try {

            val coords = if (isHard){
                val split = strCoords.split(' ').map { it.toInt()}
                Pair(split[0], split[1])
            } else {
                Pair(placements.size + 1, strCoords.toInt())
            }

            if (isPlacementCompleted()) return false
            if (placements.contains(coords)) return false
            if (coords.first !in 1..QUEEN_NUM || coords.second !in 1..QUEEN_NUM) return false

            placements.add(coords)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun randomGeneration(){
        val rand = Random(System.currentTimeMillis())
        placements.clear()
        var i = 1
        while (i < QUEEN_NUM + 1) {
            val coords = if (isHard) Pair(rand.nextInt(1, QUEEN_NUM + 1), rand.nextInt(1, QUEEN_NUM + 1))
                         else Pair(i, rand.nextInt(1, QUEEN_NUM + 1))

            if (!placements.contains(coords)) {
                placements.add(coords)
                i++
            }
        }
    }

    fun inputIsLDFS(): Boolean{
        var text: String
        do {
            print("Enter algorithm type (a*/ldfs): ")
            text = readLine()!!.uppercase()
        } while (text != "A*" && text != "LDFS")
        return text == "LDFS"
    }
}
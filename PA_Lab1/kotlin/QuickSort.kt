import kotlin.random.Random

fun partition(arr : IntArray, low: Int, high: Int): Int{
    var i = low - 1
    val randomIndex = Random.nextInt(low, high+1)
    arr[high] = arr[randomIndex].also { arr[randomIndex] = arr[high] }
    val pivot = arr[high]

    for (j in low until high){
        if (arr[j] <= pivot){
            i += 1
            arr[i] = arr[j].also { arr[j] = arr[i] }
        }
    }

    arr[i+1] = arr[high].also { arr[high] = arr[i+1]}
    return i + 1
}

fun quickSort(arr : IntArray, low: Int, high: Int){
    if (high == 0){
        return
    }
    if (low < high){
        val p = partition(arr, low, high)
        quickSort(arr, low, p-1)
        quickSort(arr, p+1, high)
    }
}
import java.lang.Integer.min

class Node(
    var children: ArrayList<Node> = ArrayList(0),
    var records: ArrayList<Record> = ArrayList(0),
    var parent: Node? = null): java.io.Serializable{

    fun isAtMax(): Boolean{
        return this.records.size == 2*T_VALUE - 1
    }

    fun isAtMin(): Boolean{
        return this.records.size == T_VALUE - 1
    }

    fun isLowerThanMin(): Boolean{
        return this.records.size < T_VALUE - 1
    }

    fun isEmpty(): Boolean{
        return records.isEmpty()
    }

    fun addRecord(record: Record){ // TODO O(n)
        for (i in records.indices){
            if (records[i].key > record.key){
                records.add(i, record)
                return
            }
        }
        records.add(record)
    }

    fun removeChild(node: Node){ // TODO O(n)
        children.remove(node)
    }


    fun replaceRecordByKey(key: Int, replacement: Record){
        val record = binarySearchKey(key)
        record?.let{
            val recordIndex = records.indexOf(it) // TODO O(n)
            records[recordIndex] = replacement
        }
    }

    fun getLastRecord(): Record{
        return records.last()
    }

    fun getFirstRecord(): Record{
        return records.first()
    }

    fun getLastChild(): Node{
        return children.last()
    }

    fun getFirstChild(): Node{
        return children.first()
    }

    fun extractLastRecord(): Record{
        return records.removeLast()
    }

    fun extractFirstRecord(): Record{
        return records.removeFirst()
    }

    fun extractLastChild(): Node{
        return children.removeLast()
    }

    fun extractFirstChild(): Node{
        return children.removeFirst()
    }

    fun removeRecord(record: Record){
        records.remove(record) // TODO O(n)
    }

    fun removeRecordByKey(key: Int){
        val record = binarySearchKey(key)
        record?.let{
            records.remove(record) // TODO O(n)
        }
    }


    fun recordByChild(child: Node): Record{
        val index = min(records.size - 1, children.indexOf(child))
        return records[index] // TODO O(n)
    }

    fun childByRecord(record: Record): Node{
        return children[records.indexOf(record)] // TODO O(n)
    }

    fun childByKey(key: Int): Node{ // TODO O(n)
        for (i in records.indices){
            if (records[i].key >= key){
                return children[i]
            }
        }
        return children.last()
    }


    fun leftSibling(): Node?{
        if (isRoot() || parent!!.children.first() == this) return null
        return parent!!.children[parent!!.children.indexOf(this) - 1]
    }

    fun rightSibling(): Node?{
        if (isRoot() || parent!!.children.last() == this) return null
        return parent!!.children[parent!!.children.indexOf(this) + 1]
    }

    fun isLeaf(): Boolean{
        return children.isEmpty()
    }

    fun isRoot(): Boolean{
        return parent == null
    }

    fun hasKey(key: Int): Boolean{
        return binarySearchKey(key) != null
    }

    fun binarySearchKey(key: Int): Record? {
        var low = 0
        var high = records.size - 1
        while (low <= high) {
            val mid = (high - low) / 2 + low
            if (records[mid].key > key) {
                high = mid - 1
            }
            else if (records[mid].key == key) {
                return records[mid]
            }
            else {
                low = mid + 1
            }
        }
        return null
    }

    override fun toString(): String{
        return "\nchildren=$children:records=$records:parent=${parent?.records}"
    }
}

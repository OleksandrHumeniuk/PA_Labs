class Tree: java.io.Serializable{
    var root: Node? = null

    fun isEmpty(): Boolean{
        return root == null
    }

    fun search(key: Int): Record?{
        if (isEmpty()) return null
        val node = searchNode(root!!, key) ?: return null
        return node.binarySearchKey(key)
    }

    private fun searchNode(node: Node, key: Int): Node?{
        return if (node.hasKey(key)) node
        else if (node.isLeaf()) null
        else{
            searchNode(node.childByKey(key), key)
        }
    }

    fun insert(record: Record){
        if (isEmpty()){
            root = Node()
            root!!.addRecord(record)
        }
        else{
            var node = root
            while (!node!!.isLeaf()) {
                node = node.childByKey(record.key)
            }
            node.addRecord(record)
            restorePropertyInsert(node)
        }
    }

    private fun restorePropertyInsert(node: Node){
        if (node.isAtMax()){
            val node1Children = ArrayList(node.children.take(T_VALUE))
            val node1Records = ArrayList(node.records.take(T_VALUE - 1))
            val node1 = Node(node1Children, node1Records, node.parent)
            for (child in node1Children) child.parent = node1

            val node2Children = ArrayList(node.children.takeLast(T_VALUE))
            val node2Records = ArrayList(node.records.takeLast(T_VALUE - 1))
            val node2 = Node(node2Children, node2Records, node.parent)
            for (child in node2Children) child.parent = node2 //TODO Don't like this parent/child

            if (node.isRoot()){
                val newRootChildren = arrayListOf(node1, node2)
                val newRootRecords = arrayListOf(node.records[T_VALUE - 1])
                val newRoot = Node(newRootChildren, newRootRecords)
                root = newRoot
                node1.parent = newRoot
                node2.parent = newRoot
            }

            else{
                node.parent!!.removeChild(node)
                node.parent!!.addRecord(node.records[T_VALUE-1])
                node.parent!!.children.addAll(node.parent!!.records.indexOf(node.records[T_VALUE-1]), arrayListOf(node1, node2)) //TODO Jesus wtf is this :)
                restorePropertyInsert(node.parent!!)
            }
        }
    }

    fun delete(key: Int): Boolean{
        log("Delete is called Key=$key")
        if (isEmpty()) return false
        val node = searchNode(root!!, key) ?: return false
        if (node.isLeaf()) removeLeafKey(node, key)
        else {
            val leftChild = node.childByKey(key)
            if (!leftChild.isAtMin()){
                log("delete -- finding node in left child")
                val predecessor = findPredecessor(leftChild)
                val predecessorRecord = predecessor.getLastRecord()
                node.replaceRecordByKey(key, predecessorRecord)
                removeLeafKey(predecessor, predecessorRecord.key)
            }
            else {
                log("delete -- finding node in right child")
                val rightChild = leftChild.rightSibling()
                val successor = findSuccessor(rightChild!!)
                val successorRecord = successor.getFirstRecord()
                node.replaceRecordByKey(key, successorRecord)
                removeLeafKey(successor, successorRecord.key)
            }
        }
        return true
    }

    private fun findPredecessor(node: Node): Node{
        var predecessor = node
        while (! predecessor.isLeaf()) predecessor = predecessor.getLastChild()
        log("findingPredecessor of node $node. \n foundNode=$predecessor")
        return predecessor
    }

    private fun findSuccessor(node: Node): Node{
        var successor = node
        while (! successor.isLeaf()) successor = successor.getFirstChild()
        log("findingSuccessor of node $node. \n foundNode=$successor")
        return successor
    }

    private fun removeLeafKey(node: Node, key: Int){
        log("removeLeaf is called with node=$node \n and key=$key")
        node.removeRecordByKey(key)
        restorePropertyDelete(node)
    }

    private fun restorePropertyDelete(node: Node){
        log("restorePropertyDelete is called with node=$node")
        if (node.isLowerThanMin()){
            log("restorePropertyDelete -- node needs to restore")
            if (node.isRoot()){
                log("restorePropertyDelete -- restored node is a root")
                if (node.isEmpty()) {
                    root = node.children.first()
                    root!!.parent = null
                }
            }
            else if(!borrowFromLeft(node) && !borrowFromRight(node)){
                mergeNodes(node)
                restorePropertyDelete(node.parent!!)
            }
        }
    }

    private fun borrowFromLeft(node: Node): Boolean{
        val left = node.leftSibling()
        log("trying to borrowFromLeft for node=$node \nand left=$left")
        if (left != null && !left.isAtMin()){
            log("left has enough records to share!")
            val siblingRecord = left.extractLastRecord()
            val parentRecord = node.parent!!.recordByChild(left)
            node.parent!!.replaceRecordByKey(parentRecord.key, siblingRecord) //TODO Seems weird maybe change everything to record not key
            node.records.add(0, parentRecord)
            if (!left.isLeaf()){
                log("left isn't a leaf, sharing children")
                val siblingChild = left.extractLastChild()
                node.children.add(0, siblingChild)
            }
            log("borrowFromLeft -- updated node=$node")
            return true
        }
        log("Left doesn't have enough record to share!")
        return false
    }

    private fun borrowFromRight(node: Node): Boolean{
        val right = node.rightSibling()
        log("trying to borrowFromRight for node=$node \nand right=$right")
        if (right != null && !right.isAtMin()){
            log("right has enough records to share!")
            val siblingRecord = right.extractFirstRecord()
            val parentRecord = node.parent!!.recordByChild(right)
            node.parent!!.replaceRecordByKey(parentRecord.key, siblingRecord) //TODO Seems weird maybe change everything to record not key
            node.records.add(parentRecord)

            if (!right.isLeaf()){
                log("right isn't a leaf, sharing children")
                val siblingChild = right.extractLastChild()
                node.children.add(siblingChild)
            }
            log("borrowFromRight -- updated node=$node")
            return true
        }
        log("Right doesn't have enough record to share!")
        return false
    }

    private fun mergeNodes(node: Node){
        log("Sibling cannot share records -- running mergeNodes for node=$node")
        val left = node.leftSibling()
        if (left != null){
            log("mergeNodes -- left exists, merging with left")
            val parentRecord = node.parent!!.recordByChild(left) //TODO by record -- change to indexes?
            node.parent!!.removeRecord(parentRecord)
            node.children.addAll(0, left.children)
            node.parent!!.removeChild(left)
            node.records.add(0, parentRecord)
            node.records.addAll(0, left.records)

            for (child in left.children){ //TODO NEED TO ADD PARENTS IN PSEUDOCODE
                child.parent = node
            }

            log("result of mergeNodes -- node=$node \n and parent=${node.parent}")
        }
        else{
            log("mergeNodes -- right exists, merging with right")
            val right = node.rightSibling()
            val parentRecord = node.parent!!.recordByChild(right!!)
            node.parent!!.removeRecord(parentRecord)
            node.children.addAll(right.children)
            node.parent!!.removeChild(right)
            node.records.add(parentRecord)
            node.records.addAll(right.records)

            for (child in right.children){
                child.parent = node
            }

            log("result of mergeNodes -- node=$node \n and parent=${node.parent}")
        }

    }

    fun editRecordData(key: Int, newData: String): Boolean{
        if (isEmpty()) return false
        val node = searchNode(root!!, key) ?: return false
        node.binarySearchKey(key)?.let{
            it.data = newData
            return true
        }
        return false
    }

    fun editRecordKey(key: Int, newKey: Int): Boolean{
        if (isEmpty()) return false
        val node = searchNode(root!!, key) ?: return false
        val data = node.binarySearchKey(key)!!.data
        delete(key)
        insert(Record(newKey, data))
        return true
    }

    fun printTree(){
        println()
        println()
        for (i in 0..100) print("#")
        println()
        if (isEmpty()){
            println("Empty tree!")
        }
        else{
            print("Printing tree:")
            printNode(root!!, 0)
        }
    }

    private fun printNode(node: Node, level: Int){
        val mid = node.children.size / 2
        if (!node.isLeaf()){
            for (i in node.children.size-1 downTo mid){
                printNode(node.children[i], level + 1)
            }
        }
        println()
        for(i in 0 until 5*level){
            print(" ")
        }
        print("-> |${node.records}|")
        if (!node.isLeaf()){
            for (i in mid -1  downTo 0 ){
                printNode(node.children[i], level + 1)
            }
        }
    }

//    fun getFirstNodes(number: Int): ArrayList<ArrayList<Node>>{ //TODO FROM GUI PROJECT. Maybe will need later?
//        val list = ArrayList<ArrayList<Node>>(0)
//        if (isEmpty()) return list
//        else{
//            val queue = arrayListOf(root!!)
//            var childrenCounter = 0
//            var currentDepth = 0
//            val currentList = ArrayList<Node>(0)
//            while (queue.isNotEmpty() && childrenCounter < number){
//                val current = queue.removeFirst()
//
//                if (current.depth > currentDepth){
//                    list.add(currentList)
//                    currentList.clear()
//                    currentDepth = current.depth
//                }
//                currentList.add(current)
//                queue.addAll(current.getChildren())
//                childrenCounter += current.childrenLength
//            }
//            println(currentList)
//            return list
//        }
//    }
}

















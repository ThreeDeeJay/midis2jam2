package org.wysko.midis2jam2

class HeapAPQ<K, V>(private val comparator: Comparator<K>) {
    private val entries = mutableListOf<PQEntry>()

    /**
     * Inserts an entry into this APQ. The APQ will restore heap order.
     *
     * @param key The key of the entry.
     * @param value The value of the entry.
     */
    fun insert(key: K, value: V): PQEntry {
        val index = entries.size
        return PQEntry(key, value, index).let {
            entries.add(it)
            upHeap(index)
            it
        }
    }

    /**
     * Returns the head of this APQ and removes it from the tree.
     *
     * @return The head of this APQ, or `null` if the APQ is empty.
     */
    fun removeMin(): PQEntry? {
        if (entries.isEmpty()) return null
        val entry: PQEntry = entries[0]
        val e = entries.removeLast()
        if (entries.isNotEmpty()) {
            entries[0] = e
            e.index = 0
            downHeap(0)
        }
        return entry
    }

    /**
     * Returns the head of this APQ, without removing it.
     */
    fun min(): PQEntry? = entries.firstOrNull()

    /**
     * The data of this heap.
     */
    fun data(): MutableList<PQEntry> = entries

    /**
     * The size of this heap.
     */
    fun size(): Int = entries.size

    /**
     * Clears this heap.
     */
    fun clear(): Unit = entries.clear()

    private fun upHeap(index: Int) {
        val target = entries[index]
        target.parent()?.let { parent ->
            if (comparator.compare(target.key, parent.key) < 0) {
                val parentIndex = parent.index
                swapEntries(index, parentIndex)
                upHeap(parentIndex)
            }
        }
    }

    private fun downHeap(index: Int) {
        val entry = entries[index]
        entry.leftChild()?.let {
            val smallestChildIndex: Int = (entry.smallestChild() ?: return@let).index

            if (comparator.compare(
                    entry.key,
                    entries[smallestChildIndex].key
                ) > 0
            ) { // This entry is larger than the smallest child
                swapEntries(entry.index, smallestChildIndex)
                downHeap(smallestChildIndex)
            }
        }
    }

    private fun swapEntries(i1: Int, i2: Int) {
        val e1 = entries[i1]
        val e2 = entries[i2]
        entries[i1] = e2
        entries[i2] = e1
        e1.index = i2
        e2.index = i1
    }

    /**
     * An entry within this priority queue.
     *
     * @property key The key of the entry.
     * @property value The value of the entry.
     * @property index The index of this element within the list of elements. Improves efficiency.
     */
    inner class PQEntry(val key: K, val value: V, var index: Int) {
        /**
         * Returns the left child of this entry, or `null` if it does not exist.
         */
        internal fun leftChild(): PQEntry? = when {
            entries.size <= 2 * index + 1 -> null
            else -> entries[2 * index + 1]
        }

        /**
         * Returns the right child of this entry, or `null` if it does not exist.
         */
        private fun rightChild(): PQEntry? = when {
            entries.size <= 2 * index + 2 -> null
            else -> entries[2 * index + 2]
        }

        /**
         * Returns the parent of this entry, or `null` if it does not exist.
         */
        internal fun parent(): PQEntry? = when (index) {
            0 -> null
            else -> entries[(index - 1) / 2]
        }

        /**
         * Returns the smallest child of this entry, or `null` if this entry does not have any children.
         */
        internal fun smallestChild(): PQEntry? {
            val set = setOf(leftChild(), rightChild()).filterNotNull()
            return when {
                set.isEmpty() -> null
                set.size == 1 -> set.first()
                else -> if (comparator.compare(set[0].key, set[1].key) < 0) set[0] else set[1]
            }
        }
    }
}

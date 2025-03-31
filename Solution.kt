
class Solution {

    private companion object {
        const val BIT_SET_SUBGROUPS = 2
        val RANGE_OF_VALUES = intArrayOf(1, 100)
    }

    private var minNumberOfUniqueValueConnections = 0

    fun numberOfComponents(properties: Array<IntArray>, minNumberOfUniqueValueConnections: Int): Int {
        this.minNumberOfUniqueValueConnections = minNumberOfUniqueValueConnections
        val bitSetGroups: Array<LongArray> = createBitSetGroups(properties)
        return findNumberOfConnectedGroups(properties, bitSetGroups)
    }

    private fun createBitSetGroups(properties: Array<IntArray>): Array<LongArray> {
        /*
        To keep the number of bit shifts within a 64-bit integer, each bitSetGroups[i] has:
        bitSetGroups[i][0] => stores unique values from 1 to 50
        bitSetGroups[i][1] => stores unique values from 51 to 100
         */
        val bitSetGroups = Array(properties.size) { LongArray(BIT_SET_SUBGROUPS) }

        for (i in properties.indices) {
            for (value in properties[i]) {

                val index = value / (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1)
                val bitShifts = 1 + value % (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1)
                val bitStamp : Long = 1L shl bitShifts

                bitSetGroups[i][index] = bitSetGroups[i][index] or bitStamp
            }
        }
        return bitSetGroups
    }

    private fun findNumberOfConnectedGroups(properties: Array<IntArray>, bitSetGroups: Array<LongArray>): Int {
        val unionFind = UnionFind(properties.size)
        var numberOfComponents = properties.size

        for (i in bitSetGroups.indices) {
            for (j in i + 1..<bitSetGroups.size) {

                var totalConnections = 0
                for (g in 0..<BIT_SET_SUBGROUPS) {
                    totalConnections += countBitsSetToOne(bitSetGroups[i][g] and bitSetGroups[j][g])
                }

                if (totalConnections >= minNumberOfUniqueValueConnections && unionFind.joinByRank(i, j)) {
                    --numberOfComponents
                }
            }
        }
        return numberOfComponents
    }

    private fun countBitsSetToOne(value: Long): Int {
        var bitsSetToOne = 0
        var value: Long = value
        while (value > 0) {
            bitsSetToOne += (value and 1).toInt()
            value = value shr 1
        }
        return bitsSetToOne
    }
}

class UnionFind(private val numberOfElements: Int) {

    private val parent = IntArray(numberOfElements)
    private val rank = IntArray(numberOfElements)

    init {
        for (i in 0..<numberOfElements) {
            parent[i] = i
            rank[i] = 1
        }
    }
    /*
     Alternatively:
     private val parent = IntArray(numberOfElements) { i -> i }
     private val rank = IntArray(numberOfElements) { 1 }
     */

    private fun findParent(index: Int): Int {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index])
        }
        return parent[index]
    }

    fun joinByRank(indexOne: Int, indexTwo: Int): Boolean {
        val first = findParent(indexOne)
        val second = findParent(indexTwo)
        if (first == second) {
            return false
        }

        if (rank[first] >= rank[second]) {
            rank[first] += rank[second]
            parent[second] = first
        } else {
            rank[second] += rank[first]
            parent[first] = second
        }
        return true
    }
}

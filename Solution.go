
package main

const BIT_SET_SUBGROUPS = 2
var RANGE_OF_VALUES = [2]int{1, 100}
var minNumberOfUniqueValueConnections int

func numberOfComponents(properties [][]int, minNumberOfConnections int) int {
    minNumberOfUniqueValueConnections = minNumberOfConnections
    var bitSetGroups [][]int64 = createBitSetGroups(properties)
    return findNumberOfConnectedGroups(properties, bitSetGroups)
}

func createBitSetGroups(properties [][]int) [][]int64 {
    /*
     To keep the number of bit shifts within a 64-bit integer, each bitSetGroups[i] has:
     bitSetGroups[i][0] => stores unique values from 1 to 50
     bitSetGroups[i][1] => stores unique values from 51 to 100
    */
    bitSetGroups := make([][]int64, len(properties))

    for i := range properties {

        bitSetGroups[i] = make([]int64, BIT_SET_SUBGROUPS)

        for _, value := range properties[i] {

            index := value / (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1)
            bitShifts := 1 + value % (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS+1)
            bitStamp := int64(1) << bitShifts

            bitSetGroups[i][index] |= bitStamp
        }
    }
    return bitSetGroups
}

func findNumberOfConnectedGroups(properties [][]int, bitSetGroups [][]int64) int {
    unionFind := NewUnionFind(len(properties))
    numberOfComponents := len(properties)

    for i := range bitSetGroups {
        for j := i + 1; j < len(bitSetGroups); j++ {

            totalConnections := 0
            for g := range BIT_SET_SUBGROUPS {
                totalConnections += countBitsSetToOne(bitSetGroups[i][g] & bitSetGroups[j][g])
            }

            if totalConnections >= minNumberOfUniqueValueConnections && unionFind.joinByRank(i, j) {
                numberOfComponents--
            }
        }
    }
    return numberOfComponents
}

func countBitsSetToOne(value int64) int {
    bitsSetToOne := 0
    for value > 0 {
        bitsSetToOne += int(value & 1)
        value >>= 1
    }
    return bitsSetToOne
}

type UnionFind struct {
    parent []int
    rank   []int
}

func NewUnionFind(numberOfElements int) *UnionFind {
    unionFind := &UnionFind{
        parent: make([]int, numberOfElements),
        rank:   make([]int, numberOfElements),
    }
    for i := range numberOfElements {
        unionFind.parent[i] = i
        unionFind.rank[i] = 1
    }
    return unionFind
}

func (this *UnionFind) findParent(index int) int {
    if this.parent[index] != index {
        this.parent[index] = this.findParent(this.parent[index])
    }
    return this.parent[index]
}

func (this *UnionFind) joinByRank(indexOne int, indexTwo int) bool {
    first := this.findParent(indexOne)
    second := this.findParent(indexTwo)
    if first == second {
        return false
    }

    if this.rank[first] >= this.rank[second] {
        this.rank[first] += this.rank[second]
        this.parent[second] = first
    } else {
        this.rank[second] += this.rank[first]
        this.parent[first] = second
    }
    return true
}

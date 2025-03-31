
/**
 * @param {number[][]} properties
 * @param {number} minNumberOfUniqueValueConnections
 * @return {number}
 */
var numberOfComponents = function (properties, minNumberOfUniqueValueConnections) {
    this.BIT_SET_SUBGROUPS = 4;
    this.RANGE_OF_VALUES = [1, 100];
    this.minNumberOfUniqueValueConnections = minNumberOfUniqueValueConnections;

    const bitSetGroups = createBitSetGroups(properties);
    return findNumberOfConnectedGroups(properties, bitSetGroups);
};

/**
 * @param {number[][]} properties
 * @return {number[][]}
 */
function createBitSetGroups(properties) {
    /*
     To keep the number of bit shifts within a 32-bit integer, each bitSetGroups[i] has: 
     bitSetGroups[i][0] => stores unique values from 1 to 25 
     bitSetGroups[i][1] => stores unique values from 26 to 50 
     bitSetGroups[i][2] => stores unique values from 51 to 75 
     bitSetGroups[i][3] => stores unique values from 76 to 100 
     
     While the solutions in the other languages keep the number of bit shifts within a 64-bit integer,
     thus bitSetGroups[i] is divided into two parts, from 1 to 50 and from 51 to 100, in JavaScript, 
     correspondingly in TypeScript, when performing bitwise operations on their standard number, 
     implemented in double-precision 64-bit binary format IEEE 754, it is treated in a 32-bit binary format. 
     
     To avoid overflowing, one option is to apply their inbuilt BigInt class (the less efficient option) 
     or, as is done here, to divide the bitSetGroups[i] into four equal parts, 
     so that the bit shifts do not exceed the 32-bit.
     */
    const bitSetGroups = Array.from(new Array(properties.length), () => new Array(this.BIT_SET_SUBGROUPS).fill(0));

    for (let i = 0; i < properties.length; ++i) {
        for (let value of properties[i]) {

            const index = Math.floor(value / (this.RANGE_OF_VALUES[1] / this.BIT_SET_SUBGROUPS + 1));
            const bitShifts = 1 + (value % (this.RANGE_OF_VALUES[1] / this.BIT_SET_SUBGROUPS + 1));
            const bitStamp = 1 << bitShifts;

            bitSetGroups[i][index] |= bitStamp;
        }
    }
    return bitSetGroups;
}

/**
 * @param {number[][]} properties
 * @param {number[][]} bitSetGroups
 * @return {number}
 */
function findNumberOfConnectedGroups(properties, bitSetGroups) {
    const unionFind = new UnionFind(properties.length);
    let numberOfComponents = properties.length;

    for (let i = 0; i < bitSetGroups.length; ++i) {
        for (let j = i + 1; j < bitSetGroups.length; ++j) {

            let totalConnections = 0;
            for (let g = 0; g < this.BIT_SET_SUBGROUPS; ++g) {
                totalConnections += countBitsSetToOne(bitSetGroups[i][g] & bitSetGroups[j][g]);
            }

            if (totalConnections >= this.minNumberOfUniqueValueConnections && unionFind.joinByRank(i, j)) {
                --numberOfComponents;
            }
        }
    }
    return numberOfComponents;
}

/**
 * @param {number} value
 * @return {number}
 */
function  countBitsSetToOne(value) {
    let bitsSetToOne = 0;
    while (value > 0) {
        bitsSetToOne += (value & 1);
        value >>= 1;
    }
    return bitsSetToOne;
}


class UnionFind {

    #parent;
    #rank;

    /**
     * @param {number} numberOfElements
     */
    constructor(numberOfElements) {
        this.#parent = new Array(numberOfElements);
        this.#rank = new Array(numberOfElements);
        for (let i = 0; i < numberOfElements; ++i) {
            this.#parent[i] = i;
            this.#rank[i] = 1;
        }
        /*
         Alternatively:
         this.parent = Array.from(Array(numberOfElements).keys());
         this.rank = new Array(numberOfElements).fill(1);
         */
    }

    /**
     * @param {number} index
     * @return {number}
     */
    #findParent(index) {
        if (this.#parent[index] !== index) {
            this.#parent[index] = this.#findParent(this.#parent[index]);
        }
        return this.#parent[index];
    }

    /**
     * @param {number} indexOne
     * @param {number} indexTwo
     * @return {boolean}
     */
    joinByRank(indexOne, indexTwo) {
        const first = this.#findParent(indexOne);
        const second = this.#findParent(indexTwo);
        if (first === second) {
            return false;
        }

        if (this.#rank[first] >= this.#rank[second]) {
            this.#rank[first] += this.#rank[second];
            this.#parent[second] = first;
        } else {
            this.#rank[second] += this.#rank[first];
            this.#parent[first] = second;
        }
        return true;
    }
}

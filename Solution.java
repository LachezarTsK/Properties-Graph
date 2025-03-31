
public class Solution {

    private static final int BIT_SET_SUBGROUPS = 2;
    private static final int[] RANGE_OF_VALUES = {1, 100};
    private int minNumberOfUniqueValueConnections;

    public int numberOfComponents(int[][] properties, int minNumberOfUniqueValueConnections) {
        this.minNumberOfUniqueValueConnections = minNumberOfUniqueValueConnections;
        long[][] bitSetGroups = createBitSetGroups(properties);
        return findNumberOfConnectedGroups(properties, bitSetGroups);
    }

    private long[][] createBitSetGroups(int[][] properties) {
        /*
        To keep the number of bit shifts within a 64-bit integer, each bitSetGroups[i] has: 
        bitSetGroups[i][0] => stores unique values from 1 to 50 
        bitSetGroups[i][1] => stores unique values from 51 to 100 
         */
        long[][] bitSetGroups = new long[properties.length][BIT_SET_SUBGROUPS];

        for (int i = 0; i < properties.length; ++i) {
            for (int value : properties[i]) {

                int index = value / (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1);
                int bitShifts = 1 + value % (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1);
                long bitStamp = 1L << bitShifts;

                bitSetGroups[i][index] |= bitStamp;
            }
        }
        return bitSetGroups;
    }

    private int findNumberOfConnectedGroups(int[][] properties, long[][] bitSetGroups) {
        UnionFind unionFind = new UnionFind(properties.length);
        int numberOfComponents = properties.length;

        for (int i = 0; i < bitSetGroups.length; ++i) {
            for (int j = i + 1; j < bitSetGroups.length; ++j) {

                int totalConnections = 0;
                for (int g = 0; g < BIT_SET_SUBGROUPS; ++g) {
                    totalConnections += countBitsSetToOne(bitSetGroups[i][g] & bitSetGroups[j][g]);
                }

                if (totalConnections >= minNumberOfUniqueValueConnections && unionFind.joinByRank(i, j)) {
                    --numberOfComponents;
                }
            }
        }
        return numberOfComponents;
    }

    private int countBitsSetToOne(long value) {
        int bitsSetToOne = 0;
        while (value > 0) {
            bitsSetToOne += (value & 1);
            value >>= 1;
        }
        return bitsSetToOne;
    }
}

class UnionFind {

    private int[] parent;
    private int[] rank;

    UnionFind(int numberOfElements) {
        parent = new int[numberOfElements];
        rank = new int[numberOfElements];
        for (int i = 0; i < numberOfElements; ++i) {
            parent[i] = i;
            rank[i] = 1;
        }
        /*
        Alternatively:
        parent = IntStream.range(0, numberOfElements).toArray();
        rank = new int[numberOfElements];
        Arrays.fill(rank, 1);
         */
    }

    private int findParent(int index) {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index]);
        }
        return parent[index];
    }

    boolean joinByRank(int indexOne, int indexTwo) {
        int first = findParent(indexOne);
        int second = findParent(indexTwo);
        if (first == second) {
            return false;
        }

        if (rank[first] >= rank[second]) {
            rank[first] += rank[second];
            parent[second] = first;
        } else {
            rank[second] += rank[first];
            parent[first] = second;
        }
        return true;
    }
}


#include <span>
#include <vector>
using namespace std;

class UnionFind {

    vector<int> parent;
    vector<int> rank;

public:
    UnionFind(int numberOfElements) {
        parent.resize(numberOfElements);
        rank.resize(numberOfElements);
        for (int i = 0; i < numberOfElements; ++i) {
            parent[i] = i;
            rank[i] = 1;
        }
        /*
        Alternatively:
        parent.resize(numberOfElements);
        ranges::iota(parent, 0);
        rank.resize(numberOfElements, 1);
        */
    }

    int findParent(int index) {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index]);
        }
        return parent[index];
    }

    bool joinByRank(int indexOne, int indexTwo) {
        int first = findParent(indexOne);
        int second = findParent(indexTwo);
        if (first == second) {
            return false;
        }

        if (rank[first] >= rank[second]) {
            rank[first] += rank[second];
            parent[second] = first;
        }
        else {
            rank[second] += rank[first];
            parent[first] = second;
        }
        return true;
    }
};

class Solution {

    static const int BIT_SET_SUBGROUPS = 2;
    static constexpr array<int, 2> RANGE_OF_VALUES = { 1, 100 };
    int minNumberOfUniqueValueConnections{};

public:
    int numberOfComponents(vector<vector<int>>& properties, int minNumberOfUniqueValueConnections) {
        this->minNumberOfUniqueValueConnections = minNumberOfUniqueValueConnections;
        vector<vector<long long>> bitSetGroups = createBitSetGroups(properties);
        return findNumberOfConnectedGroups(properties, bitSetGroups);
    }

private:
    vector<vector<long long>> createBitSetGroups(span<const vector<int>> properties) const {
        /*
        To keep the number of bit shifts within a 64-bit integer, each bitSetGroups[i] has:
        bitSetGroups[i][0] => stores unique values from 1 to 50
        bitSetGroups[i][1] => stores unique values from 51 to 100
         */
        vector<vector<long long>> bitSetGroups(properties.size(), vector<long long>(BIT_SET_SUBGROUPS)); ;

        for (int i = 0; i < properties.size(); ++i) {
            for (int value : properties[i]) {

                int index = value / (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1);
                int bitShifts = 1 + value % (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1);
                long long bitStamp = 1LL << bitShifts;

                bitSetGroups[i][index] |= bitStamp;
            }
        }
        return bitSetGroups;
    }

    int findNumberOfConnectedGroups(span<const vector<int>> properties, span<const vector<long long>> bitSetGroups) const {
        UnionFind unionFind(properties.size());
        int numberOfComponents = properties.size();

        for (int i = 0; i < bitSetGroups.size(); ++i) {
            for (int j = i + 1; j < bitSetGroups.size(); ++j) {

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

    int countBitsSetToOne(long long value) const {
        int bitsSetToOne = 0;
        while (value > 0) {
            bitsSetToOne += (value & 1);
            value >>= 1;
        }
        return bitsSetToOne;
    }
};

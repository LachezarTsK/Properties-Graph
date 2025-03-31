
using System;

public class Solution
{
    private static readonly int BIT_SET_SUBGROUPS = 2;
    private static readonly int[] RANGE_OF_VALUES = { 1, 100 };
    private int minNumberOfUniqueValueConnections;

    public int NumberOfComponents(int[][] properties, int minNumberOfUniqueValueConnections)
    {
        this.minNumberOfUniqueValueConnections = minNumberOfUniqueValueConnections;
        long[][] bitSetGroups = CreateBitSetGroups(properties);
        return FindNumberOfConnectedGroups(properties, bitSetGroups);
    }

    private long[][] CreateBitSetGroups(int[][] properties)
    {
        /*
        To keep the number of bit shifts within a 64-bit integer, each bitSetGroups[i] has: 
        bitSetGroups[i][0] => stores unique values from 1 to 50 
        bitSetGroups[i][1] => stores unique values from 51 to 100 
         */
        long[][] bitSetGroups = new long[properties.Length][];

        for (int i = 0; i < properties.Length; ++i)
        {
            bitSetGroups[i] = new long[BIT_SET_SUBGROUPS];

            foreach (int value in properties[i])
            {

                int index = value / (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1);
                int bitShifts = 1 + value % (RANGE_OF_VALUES[1] / BIT_SET_SUBGROUPS + 1);
                long bitStamp = 1L << bitShifts;

                bitSetGroups[i][index] |= bitStamp;
            }
        }
        return bitSetGroups;
    }

    private int FindNumberOfConnectedGroups(int[][] properties, long[][] bitSetGroups)
    {
        UnionFind unionFind = new UnionFind(properties.Length);
        int numberOfComponents = properties.Length;

        for (int i = 0; i < bitSetGroups.Length; ++i)
        {
            for (int j = i + 1; j < bitSetGroups.Length; ++j)
            {

                int totalConnections = 0;
                for (int g = 0; g < BIT_SET_SUBGROUPS; ++g)
                {
                    totalConnections += CountBitsSetToOne(bitSetGroups[i][g] & bitSetGroups[j][g]);
                }

                if (totalConnections >= minNumberOfUniqueValueConnections && unionFind.JoinByRank(i, j))
                {
                    --numberOfComponents;
                }
            }
        }
        return numberOfComponents;
    }

    private int CountBitsSetToOne(long value)
    {
        int bitsSetToOne = 0;
        while (value > 0)
        {
            bitsSetToOne += (int)(value & 1);
            value >>= 1;
        }
        return bitsSetToOne;
    }
}

class UnionFind
{

    private int[] parent;
    private int[] rank;

    public UnionFind(int numberOfElements)
    {
        parent = new int[numberOfElements];
        rank = new int[numberOfElements];
        for (int i = 0; i < numberOfElements; ++i)
        {
            parent[i] = i;
            rank[i] = 1;
        }
        /*
         Alternatively:
         parent = Enumerable.Range(0, numberOfElements).ToArray();
         rank = new int[numberOfElements];
         Array.Fill(rank, 1);
         */
    }

    private int FindParent(int index)
    {
        if (parent[index] != index)
        {
            parent[index] = FindParent(parent[index]);
        }
        return parent[index];
    }

    public bool JoinByRank(int indexOne, int indexTwo)
    {
        int first = FindParent(indexOne);
        int second = FindParent(indexTwo);
        if (first == second)
        {
            return false;
        }

        if (rank[first] >= rank[second])
        {
            rank[first] += rank[second];
            parent[second] = first;
        }
        else
        {
            rank[second] += rank[first];
            parent[first] = second;
        }
        return true;
    }
}

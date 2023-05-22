package utils;


import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Stream;

public class DataStructureUtils
{

    static <T> T[][] SingleArrayToTwoDimBy8(T[] arr)
    {
        // suppressing the warning because the cast is guaranteed to succeed.
        @SuppressWarnings("unchecked")
        T[][] splitArr = (T[][]) new Object[(int) Math.ceil(arr.length / 8.0)][8];

        int rowCounter = 0;
        int clmCounter = 0;
        for (T e : arr)
        {
            splitArr[rowCounter][clmCounter] = e;
            clmCounter++;

            if(clmCounter == splitArr[0].length)
            {
                rowCounter++;
                clmCounter = 0;
            }
        }

        return splitArr;
    }


    static <T> T[] twoDimArrToSingle(T[][] arr)
    {

        // suppressing the warning because the cast is guaranteed to succeed.
        @SuppressWarnings("unchecked")
        T[] concatArr = (T[]) new Object[arr.length * arr[0].length];

        int counter = 0;
        for (T[] subArr : arr)
            for (T elem : subArr)
                concatArr[counter++] = elem;

        return concatArr;
    }


    public static Queue<Integer> createRandomQueue(int size, int seed)
    {
        Random rnd = new Random(seed);
        Integer[] arr = Stream.iterate(0, e -> e + 1)
                .limit(size)
                .toArray(Integer[]::new);

        for(int i = 0; i < arr.length / 3; i++)
        {
            int idx = rnd.nextInt(size);
            int temp = arr[idx];
            arr[idx] = arr[i];
            arr[i] = temp;
        }

        Queue<Integer> shuffledQueue = new LinkedList<>();
        Collections.addAll(shuffledQueue, arr);

        return shuffledQueue;
    }


    public static double[] twoDimArrToSingle(double[][] arr)
    {
        double[] concatArr = new double[arr.length * arr[0].length];

        int counter = 0;
        for (double[] subArr : arr)
            for (double elem : subArr)
                concatArr[counter++] = elem;

        return concatArr;
    }


    public static double[][] singleArrayToTwoDimBy8(double[] arr)
    {
        double[][] splitArr = new double[(int) Math.ceil(arr.length / 8.0)][8];

        int rowCounter = 0;
        int clmCounter = 0;
        for (double e : arr)
        {
            splitArr[rowCounter][clmCounter] = e;
            clmCounter++;

            if(clmCounter == splitArr[0].length)
            {
                rowCounter++;
                clmCounter = 0;
            }
        }

        return splitArr;
    }


    public static int[][] singleArrayToTwoDimBy8(int[] arr)
    {
        int[][] splitArr = new int[(int) Math.ceil(arr.length / 8.0)][8];

        int rowCounter = 0;
        int clmCounter = 0;
        for (int e : arr)
        {
            splitArr[rowCounter][clmCounter] = e;
            clmCounter++;

            if(clmCounter == splitArr[0].length)
            {
                rowCounter++;
                clmCounter = 0;
            }
        }

        return splitArr;
    }
}

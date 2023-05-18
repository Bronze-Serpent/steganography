package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


public class MathUtils
{

    public static int[] increaseTheAvgOfElem(int[] elements, double[] multipliers, int add)
    {
        List<Integer> cannotBeAdded = getNumCannotBeAdded(elements, add);

        if (cannotBeAdded.size() == 0)
            return new int[]{elements[0]+ add, elements[1]+ add, elements[2] + add};

        List<Integer> availableIdx = new LinkedList<>();
        Collections.addAll(availableIdx, 0, 1, 2);
        availableIdx.remove(cannotBeAdded.get(0));
        int[] addedElem = new int[3];

        if (cannotBeAdded.size() == 1)
        {
            int rem = add - (255 - elements[cannotBeAdded.get(0)]);
            addedElem[cannotBeAdded.get(0)] = 255;

            int recalcAdd = (int) Math.ceil(add + rem * multipliers[cannotBeAdded.get(0)]);
            List<Integer> cannotBeAddedSec = getNumCannotBeAdded(arrWithoutElem(elements, cannotBeAdded), recalcAdd);

            if (cannotBeAddedSec.size() == 0)
            {
                addedElem[availableIdx.get(0)] = elements[availableIdx.get(0)] + recalcAdd;
                addedElem[availableIdx.get(1)] = elements[availableIdx.get(1)] + recalcAdd;
            }
            else
            {
                if (cannotBeAddedSec.size() == 1)
                {
                    int secondRem = recalcAdd - (255 - elements[cannotBeAddedSec.get(0)]);
                    addedElem[cannotBeAddedSec.get(0)] = addedElem[cannotBeAddedSec.get(0)] + (recalcAdd - secondRem);
                    int secRecalcAdd = (int) Math.ceil(add + rem * multipliers[cannotBeAdded.get(0)]
                            + secondRem * multipliers[cannotBeAddedSec.get(0)]);

                    availableIdx.remove(cannotBeAddedSec.get(0));

                    addedElem[availableIdx.get(0)] = Math.min(addedElem[availableIdx.get(0)] + secRecalcAdd, 255);
                }
                else
                {
                    addedElem[availableIdx.get(0)] = 255;
                    addedElem[availableIdx.get(1)] = 255;
                }
            }
        }
        else
        {
            if (cannotBeAdded.size() == 2)
            {
                availableIdx.remove(cannotBeAdded.get(1));
                int recalcAdd = (int) Math.ceil(add + multipliers[cannotBeAdded.get(0)] * (add - (255 - elements[cannotBeAdded.get(0)]))
                        + multipliers[cannotBeAdded.get(1)] * (add - (255 - elements[cannotBeAdded.get(1)])));

                addedElem[cannotBeAdded.get(0)] = 255;
                addedElem[cannotBeAdded.get(1)] = 255;
                addedElem[availableIdx.get(0)] = Math.min( elements[availableIdx.get(0)] + recalcAdd, 255);
            }
            else
            {
                addedElem[cannotBeAdded.get(0)] = 255;
                addedElem[cannotBeAdded.get(1)] = 255;
                addedElem[availableIdx.get(3)] = 255;
            }
        }

        return addedElem;
    }


    public static List<List<Coordinate>> breakIntoWholeBlocks(int width, int height, int blockSize)
    {
        List<List<Coordinate>> wholeBlocks = new ArrayList<>((int) (Math.floor(height * 1.0 / blockSize)
                * Math.floor(width * 1.0 / blockSize)));

        for (int currX = 0, currY = 0; ;)
        {
            if (currY + blockSize < height)
            {
                if (currX + blockSize < width)
                {
                    List<Coordinate> block = new ArrayList<>(blockSize * blockSize);
                    for (int y = currY; y < currY + blockSize; y++)
                        for(int x = currX; x < currX + blockSize; x++)
                            block.add(new Coordinate(x, y));

                    wholeBlocks.add(block);
                    currX += blockSize;
                }
                else
                {
                    currY += blockSize;
                    currX = 0;
                }
            }
            else
                break;
        }

        return wholeBlocks;
    }


    public static int numOfSquaresInACircle(int rad)
    {
        return (rad + numOfSquaresInAQuarter(rad)) * 4;
    }


    private static int numOfSquaresInAQuarter(int rad)
    {
        return Stream.iterate(rad, r -> r - 1).limit(rad).reduce(0, Integer::sum);
    }


    private static List<Integer> getNumCannotBeAdded(int[] elements, int add)
    {
        List<Integer> numbers = new LinkedList<>();

        for (int i = 0; i < elements.length; i++)
            if (elements[i] + add > 255)
                numbers.add(i);

        return numbers;
    }


    private static int[] arrWithoutElem(int[] arr, List<Integer> idxToExclude)
    {
        int[] newArr = new int[arr.length - idxToExclude.size()];

        for (int i = 0, j = 0; i < arr.length; i++)
            if (! idxToExclude.contains(i))
                newArr[j++] = arr[i];

        return newArr;
    }
}

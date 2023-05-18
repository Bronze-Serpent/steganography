package utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class CoordinateDistributor
{

    public static List<List<Coordinate>> splitIntoTwoGroupsByMask(List<Coordinate> coordinates, long mask)
    {
        List<List<Coordinate>> splitCrd = new ArrayList<>(2);
        splitCrd.add(elementsByMask(coordinates, mask));
        splitCrd.add(elementsByMask(coordinates, ~ mask));

        return splitCrd;
    }


    public static List<Coordinate> elementsByMask(List<Coordinate> coordinates, long mask)
    {
        List<Coordinate> elements = new LinkedList<>();

        for (Coordinate c : coordinates)
        {
            if (ByteBitmask.LAST_ONE.apply((byte) mask) == 1)
                elements.add(c);

            mask = mask >>> 1;
        }

        return elements;
    }

}

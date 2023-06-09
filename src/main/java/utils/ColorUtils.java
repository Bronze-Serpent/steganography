package utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

import static utils.Channel.*;


public class ColorUtils
{

    private static final double[] COLOR_RATIO = new double[]{0.299, 0.587, 0.114};


    public static void writeNewColors(BufferedImage img, List<Coordinate> block, List<Integer> colorVal, Channel channel)
    {
        if (block.size() != colorVal.size())
            throw new IllegalArgumentException("List sizes must match");

        for (int i = 0; i < block.size(); i++)
        {
            int rgb = img.getRGB(block.get(i).x(), block.get(i).y());
            int r = getChannelVal(rgb, RED);
            int g = getChannelVal(rgb, GREEN);
            int b = getChannelVal(rgb, BLUE);

            Color newColor = switch (channel) {
                case RED -> new Color(colorVal.get(i), g, b);
                case GREEN -> new Color(r, colorVal.get(i), b);
                case BLUE -> new Color(r, g, colorVal.get(i));
                case ALPHA -> new Color(r,g,b, colorVal.get(i));
            };
            img.setRGB(block.get(i).x(), block.get(i).y(), newColor.getRGB());
        }
    }


    public static double calcBrightness(Color c)
    {
        return COLOR_RATIO[0] * c.getRed() + COLOR_RATIO[1] * c.getGreen() + COLOR_RATIO[2] * c.getBlue();
    }


    public static void makeGroupABrighterThanGroupB(BufferedImage img, List<Coordinate> gA, List<Coordinate> gB)
    {
        double gABrightness = calcAvgBrightness(img, gA);
        double gBBrightness = calcAvgBrightness(img, gB);

        if (Double.compare(gABrightness - gBBrightness, 0) > -1)
            return;

        int diff = (int) (Math.round(gBBrightness - gABrightness));
        makeGroupBrighter(img, gA, COLOR_RATIO, diff + 1);
    }


    public static void makeGroupBrighter(BufferedImage img, List<Coordinate> group, double[] colorRatio, int incr)
    {
        for (Coordinate c : group)
        {
            Color oldColor = new Color(img.getRGB(c.x(), c.y()));
            int[] incChannelsVal = MathUtils.increaseTheAvgOfElem(new int[]{oldColor.getRed(), oldColor.getGreen(),
                    oldColor.getBlue()}, colorRatio, incr);
            Color newColor = new Color(incChannelsVal[0], incChannelsVal[1], incChannelsVal[2]);

            img.setRGB(c.x(), c.y(), newColor.getRGB());
        }
    }


    @Deprecated
    public static List<List<Coordinate>> splitIntoTwoBrightnessGroups(BufferedImage img, List<Coordinate> pixelCoord)
    {
        //sortByBrightness(img, pixelCoord);

        List<List<Coordinate>> splitPixels = new ArrayList<>(2);
        splitPixels.add(pixelCoord.subList(0, pixelCoord.size() / 2));
        splitPixels.add(pixelCoord.subList(pixelCoord.size() / 2, pixelCoord.size()));

        return splitPixels;
    }


    public static double calcAvgBrightness(BufferedImage img, List<Coordinate> pixelCoord)
    {
        return pixelCoord.stream()
                .map(c -> new Color(img.getRGB(c.x(), c.y())))
                .mapToDouble(ColorUtils::calcBrightness)
                .average().orElseThrow(() -> new IllegalArgumentException(
                        "it is impossible to calculate the average value. pixelCoord is empty"));
    }


    //Ascending order
    public static void sortByBrightness(BufferedImage img, List<Coordinate> pixelCoord)
    {
        pixelCoord.sort((o1, o2) -> {
            double o1Brightness = calcBrightness(new Color(img.getRGB(o1.x(), o1.y())));
            double o2Brightness = calcBrightness(new Color(img.getRGB(o2.x(), o2.y())));

            return Double.compare(o1Brightness - o2Brightness, 0);
        });

    }


    public static double calcAvgChannelValInArea(BufferedImage img, Channel channel, final int x, final int y, int q)
    {
        int sumChannels = 0;

        for (int i = 0, currY = y + q; i <= q * 2; i++)
        {
            if (currY != y)
            {
                if (isCoordinateAvailable(img, x, currY))
                    sumChannels += getChannelVal(img.getRGB(x, currY), channel);
            }
            else
                {
                    for (int j = 0, currX = x - q; j <= q * 2; j++)
                    {
                        if (isCoordinateAvailable(img, currX, currY) && currX != x)
                            sumChannels += getChannelVal(img.getRGB(currX, currY), channel);

                        currX++;
                    }
                }
            currY--;
        }

        return Math.round(sumChannels / (4.0 * q));
    }


    public static int getChannelVal(int rgb, Channel channel)
    {
        return switch (channel) {
            case RED -> (rgb >>> 16) & 0xFF;
            case GREEN -> (rgb >>> 8) & 0xFF;
            case BLUE -> rgb & 0xFF;
            case ALPHA -> (rgb >>> 24) & 0xFF;
        };
    }


    private static boolean isCoordinateAvailable(BufferedImage img, int x, int y)
    {
        return x > -1 && x < img.getWidth() && y > -1 && y < img.getHeight();
    }
}

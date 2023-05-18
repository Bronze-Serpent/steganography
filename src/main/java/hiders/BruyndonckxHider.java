package hiders;

import utils.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;


public class BruyndonckxHider implements Hider
{

    private final int blockSize;

    public BruyndonckxHider(int blockSize)
    {
        this.blockSize = blockSize;
    }


    @Override
    public BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf)
    {
        List<Long> masks = Stream.generate(() -> ThreadLocalRandom.current().nextLong())
                .limit((long) inf.length * 8 * 2)
                .toList();

        return hideInf(stegoContainer, inf, masks, blockSize);
    }


    @Override
    public byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity)
    {
        List<Long> masks = Stream.generate(() -> ThreadLocalRandom.current().nextLong())
                .limit((long) bytesQuantity * 2)
                .toList();

        return takeOutInf(stegoContainer, masks, blockSize, bytesQuantity);
    }


    @Override
    public boolean willTheInfFit(BufferedImage stegoContainer, byte[] inf)
    {
        return willTheInfFitInTheCont(stegoContainer, inf, blockSize);
    }


    public static BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf, List<Long> masks, int blockSize)
    {
        if (inf.length == 0)
            return stegoContainer;

        if (masks.size() < inf.length * 8 * 2)
            throw new IllegalArgumentException("masks not provided for all blocks");

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(inf, 1);
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), blockSize);

        int blockI = 0;
        int maskI = 0;
        for (byte b : preparedInformation)
        {
            List<List<Coordinate>> splitByBrightness = ColorUtils.splitIntoTwoBrightnessGroups(stegoContainer, wholeBlocks.get(blockI++));
            List<List<Coordinate>> splitByMask_1 = CoordinateDistributor.splitIntoTwoGroupsByMask(splitByBrightness.get(0), masks.get(maskI++));
            List<List<Coordinate>> splitByMask_2 = CoordinateDistributor.splitIntoTwoGroupsByMask(splitByBrightness.get(1), masks.get(maskI++));

            if (b == 1)
            {
                ColorUtils.makeGroupABrighterThanGroupB(stegoContainer, splitByMask_1.get(0), splitByMask_1.get(1));
                ColorUtils.makeGroupABrighterThanGroupB(stegoContainer, splitByMask_2.get(0), splitByMask_2.get(1));
            }
            else
            {
                ColorUtils.makeGroupABrighterThanGroupB(stegoContainer, splitByMask_1.get(1), splitByMask_1.get(0));
                ColorUtils.makeGroupABrighterThanGroupB(stegoContainer, splitByMask_2.get(1), splitByMask_2.get(0));
            }
        }

        return stegoContainer;
    }


    public static byte[] takeOutInf(BufferedImage stegoContainer, List<Long> masks, int blockSize, int bytesQuantity)
    {
        if (bytesQuantity == 0)
            return new byte[0];
        if (masks.size() < bytesQuantity * 8 * 2)
            throw new IllegalArgumentException("masks not provided for all blocks");

        byte[] readBites = new byte[bytesQuantity * 8];
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), blockSize);

        int blockI = 0;
        int maskI = 0;
        for (int i = 0; i < readBites.length; i++)
        {
            if (isItZeroBruyndonckx(stegoContainer, wholeBlocks.get(blockI), masks.subList(maskI, maskI + 2)))
                readBites[i] = 0;
            else
                readBites[i] = 1;

            blockI++;
            maskI += 2;
        }

        return ByteDistributor.collectBitsBy(readBites, 1);
    }


    public static boolean willTheInfFitInTheCont(BufferedImage stegoContainer, byte[] inf, int blockSize)
    {
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), blockSize);

        return wholeBlocks.size() >= inf.length;
    }


    private static boolean isItZeroBruyndonckx(BufferedImage img, List<Coordinate> block, List<Long> masks)
    {
        List<List<Coordinate>> splitByBrightness = ColorUtils.splitIntoTwoBrightnessGroups(img, block);
        List<List<Coordinate>> splitByMask_1 =  CoordinateDistributor.splitIntoTwoGroupsByMask(splitByBrightness.get(0), masks.get(0));
        List<List<Coordinate>> splitByMask_2 =  CoordinateDistributor.splitIntoTwoGroupsByMask(splitByBrightness.get(1), masks.get(1));

        return ColorUtils.calcAvgBrightness(img, splitByMask_1.get(1)) - ColorUtils.calcAvgBrightness(img, splitByMask_1.get(0)) > 1E-5 &&
                ColorUtils.calcAvgBrightness(img, splitByMask_2.get(1)) - ColorUtils.calcAvgBrightness(img, splitByMask_2.get(0)) > 1E-5;
    }

}

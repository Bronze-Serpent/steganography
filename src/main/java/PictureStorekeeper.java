import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class PictureStorekeeper
{

    public static BufferedImage putInByBruyndonckx(BufferedImage stegoContainer, byte[] information, List<Long> masks)
    {
        if (information.length == 0)
            return stegoContainer;

        if (masks.size() < information.length * 8 * 2)
            throw new IllegalArgumentException("masks not provided for all blocks");

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(information, 1);
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), 8);

        int blockI = 0;
        int maskI = 0;
        for (byte b : preparedInformation)
        {
            List<List<Coordinate>> splitByBrightness = ColorUtils.splitIntoTwoBrightnessGroups(stegoContainer, wholeBlocks.get(blockI++));
            List<List<Coordinate>> splitByMask_1 = splitIntoTwoGroupsByMask(splitByBrightness.get(0), masks.get(maskI++));
            List<List<Coordinate>> splitByMask_2 = splitIntoTwoGroupsByMask(splitByBrightness.get(1), masks.get(maskI++));

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


    public static byte[] takeOutInfByBruyndonckx(BufferedImage stegoContainer, List<Long> masks, int bytesQuantity)
    {
        if (bytesQuantity == 0)
            return new byte[0];
        if (masks.size() < bytesQuantity * 8 * 2)
            throw new IllegalArgumentException("masks not provided for all blocks");

        byte[] readBites = new byte[bytesQuantity * 8];
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), 8);

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


    public static BufferedImage putInByCutter(BufferedImage stegoContainer, byte[] information, double sglEnergy)
    {
        if (information.length == 0 || sglEnergy == 0)
            return stegoContainer;

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(information, 1);
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (byte inf : preparedInformation)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());
            Color pixelColor = new Color(stegoContainer.getRGB(x, y));

            Color modifiedColor = new Color(pixelColor.getRed(), pixelColor.getGreen(),
                    calcValForCutterMethod(pixelColor, sglEnergy, inf));

            stegoContainer.setRGB(x, y, modifiedColor.getRGB());
        }

        return stegoContainer;
    }


    public static byte[] takeOutInfByCutter(BufferedImage stegoContainer, int q, int bytesQuantity)
    {
        if (bytesQuantity == 0)
            return new byte[0];

        byte[] readBites = new byte[bytesQuantity * 8];
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (int i = 0; i < readBites.length; i++)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());

            readBites[i] = isItZeroCutter(stegoContainer, Channel.BLUE, x, y, q) ? (byte) 0 : (byte) 1;
        }

        return ByteDistributor.collectBitsBy(readBites, 1);
    }


    public static BufferedImage putIn(BufferedImage stegoContainer, List<Channel> usedChannels, byte[] information, int qInByte)
    {
        if (usedChannels.isEmpty() || information.length == 0)
            return stegoContainer;

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(information, qInByte);
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (int i = 0; ; )
        {
            for (Channel ch : usedChannels)
            {
                int x = elector.nextInt(stegoContainer.getWidth());
                int y = elector.nextInt(stegoContainer.getHeight());

                Color pixelColor = putInOneChannel(new Color(stegoContainer.getRGB(x, y), true),
                        ch, qInByte, preparedInformation[i++]);

                stegoContainer.setRGB(x, y, pixelColor.getRGB());

                if (i >= preparedInformation.length)
                    return stegoContainer;
            }
        }
    }


    public static byte[] takeOutInf(BufferedImage stegoContainer, List<Channel> usedChannels, int qInByte, int bytesQuantity)
    {
        if (usedChannels.isEmpty() || bytesQuantity == 0)
            return new byte[0];

        return ByteDistributor.collectBitsBy(readBytes(stegoContainer, usedChannels, qInByte, bytesQuantity), qInByte);
    }


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


    private static int calcValForCutterMethod(Color color, double sglEnergy, byte inf)
    {
        if (inf != 0 && inf != 1)
            throw new IllegalArgumentException("invalid parameter value 'inf'. It must be 1 or 0. 'inf'=" + inf);

        int additive = (int) Math.round(sglEnergy * ColorUtils.calcBrightness(color));
        int blueIntensity = color.getBlue();

        if (inf == 0)
            return Math.max((blueIntensity - additive), 0);
        else
            return Math.min((blueIntensity + additive), 255);
    }


    private static boolean isItZeroBruyndonckx(BufferedImage img, List<Coordinate> block, List<Long> masks)
    {
        List<List<Coordinate>> splitByBrightness = ColorUtils.splitIntoTwoBrightnessGroups(img, block);
        List<List<Coordinate>> splitByMask_1 =  splitIntoTwoGroupsByMask(splitByBrightness.get(0), masks.get(0));
        List<List<Coordinate>> splitByMask_2 =  splitIntoTwoGroupsByMask(splitByBrightness.get(1), masks.get(1));

        return ColorUtils.calcAvgBrightness(img, splitByMask_1.get(1)) - ColorUtils.calcAvgBrightness(img, splitByMask_1.get(0)) > 1E-5 &&
                ColorUtils.calcAvgBrightness(img, splitByMask_2.get(1)) - ColorUtils.calcAvgBrightness(img, splitByMask_2.get(0)) > 1E-5;
    }


    private static boolean isItZeroCutter(BufferedImage image, Channel channel, final int x, final int y, int q)
    {
        int channelVal = ColorUtils.getChannelVal(image.getRGB(x, y), channel);
        double avgChannelVal = ColorUtils.calcAvgChannelValInArea(image, channel, x, y, q);

        return channelVal < Math.round(avgChannelVal);
    }


    private static byte[] readBytes(BufferedImage stegoContainer, List<Channel> usedChannels, int qInByte, int bytesQuantity)
    {
        byte[] readBytes = new byte[(int) (Math.ceil(bytesQuantity * 8.0 / qInByte))];

        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (int i = 0; i < readBytes.length; )
        {
            for (Channel ch : usedChannels)
            {
                int x = elector.nextInt(stegoContainer.getWidth());
                int y = elector.nextInt(stegoContainer.getHeight());

                readBytes[i++] = readFromOneChannel(new Color(stegoContainer.getRGB(x, y), true), ch, qInByte);

                if (i >= readBytes.length)
                    return readBytes;
            }
        }

        return readBytes;
    }


    private static Color putInOneChannel(Color pixelColor, Channel ch, int qInByte, byte inf)
    {
        if (pixelColor == null)
            throw new IllegalArgumentException("argument 'pixelColor' is null");
        if (ch == null)
            throw new IllegalArgumentException("argument 'ch' is null");

        // this mask will always turn the first 24 bits to 0,
        // but the color channels are 8 bits long, so it doesn't matter.
        int bitmask  = Byte.toUnsignedInt(ByteBitmask.fromNum(8 - qInByte, true).getMask());

        return switch(ch) {
            case RED -> new Color(pixelColor.getRed() & bitmask | inf,
                    pixelColor.getGreen(), pixelColor.getBlue());
            case GREEN -> new Color(pixelColor.getRed(),
                    pixelColor.getGreen() & bitmask | inf, pixelColor.getBlue());
            case BLUE -> new Color(pixelColor.getRed(), pixelColor.getGreen(),
                    pixelColor.getBlue() & bitmask | inf);
            case ALPHA -> new Color(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(),
                    pixelColor.getAlpha() & bitmask | inf);
        };
    }


    // It is assumed that the information will always be hidden in the last bits
    private static byte readFromOneChannel(Color pixelColor, Channel ch, int qInByte)
    {
        if (pixelColor == null)
            throw new IllegalArgumentException("argument 'pixelColor' is null");
        if (ch == null)
            throw new IllegalArgumentException("argument 'ch' is null");

        int bitmask  = ByteBitmask.fromNum(qInByte, false).getMask();

        return switch (ch) {
            case RED -> (byte) (pixelColor.getRed() & bitmask);
            case GREEN -> (byte) (pixelColor.getGreen() & bitmask);
            case BLUE -> (byte) (pixelColor.getBlue() & bitmask);
            case ALPHA -> (byte) (pixelColor.getAlpha() & bitmask);
        };
    }
}

package hiders;

import utils.Channel;
import utils.ByteDistributor;
import utils.ColorUtils;
import utils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


public class CutterHider implements Hider
{
    private final double sglEnergy;

    private final int areaForCalcAvg;


    public CutterHider(double sglEnergy, int areaForCalcAvg)
    {
        this.sglEnergy = sglEnergy;
        this.areaForCalcAvg = areaForCalcAvg;
    }


    @Override
    public BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf)
    {
        if (inf.length == 0 || sglEnergy == 0)
            return stegoContainer;

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(inf, 1);
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (byte preparedByte : preparedInformation)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());
            Color pixelColor = new Color(stegoContainer.getRGB(x, y));

            Color modifiedColor = new Color(pixelColor.getRed(), pixelColor.getGreen(),
                    calcValForCutterMethod(pixelColor, sglEnergy, preparedByte));

            stegoContainer.setRGB(x, y, modifiedColor.getRGB());
        }

        return stegoContainer;
    }


    @Override
    public byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity)
    {
        if (bytesQuantity == 0)
            return new byte[0];

        byte[] readBites = new byte[bytesQuantity * 8];
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (int i = 0; i < readBites.length; i++)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());

            readBites[i] = isItZeroCutter(stegoContainer, Channel.BLUE, x, y, areaForCalcAvg) ? (byte) 0 : (byte) 1;
        }

        return ByteDistributor.collectBitsBy(readBites, 1);
    }


    @Override
    public boolean willTheInfFit(BufferedImage stegoContainer, byte[] inf)
    {
        return willTheInfFitInTheCont(stegoContainer, inf, areaForCalcAvg);
    }


    //the method calculates the place based on the assumption
    // that the areas for determining the average color value will not intersect.
    public static boolean willTheInfFitInTheCont(BufferedImage stegoContainer, byte[] inf, int q)
    {
        return stegoContainer.getHeight() * stegoContainer.getWidth() / MathUtils.numOfSquaresInACircle(q) >= inf.length * 8;
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


    private static boolean isItZeroCutter(BufferedImage image, Channel channel, final int x, final int y, int q)
    {
        int channelVal = ColorUtils.getChannelVal(image.getRGB(x, y), channel);
        double avgChannelVal = ColorUtils.calcAvgChannelValInArea(image, channel, x, y, q);

        return channelVal < Math.round(avgChannelVal);
    }
}

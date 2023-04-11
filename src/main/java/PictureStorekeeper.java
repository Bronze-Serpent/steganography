import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;


public class PictureStorekeeper
{

    public static void putInByCutter(BufferedImage stegoContainer, byte[] information, double sglEnergy)
    {
        if (information.length == 0)
            return;

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(information, 1);
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (byte inf : preparedInformation)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());
            Color pixelColor = new Color(stegoContainer.getRGB(x, y));

            Color modifiedColor = new Color(pixelColor.getRed(), pixelColor.getGreen(),
                    calcValForCutterMethod(stegoContainer, pixelColor, sglEnergy, inf));

            stegoContainer.setRGB(x, y, modifiedColor.getRGB());
        }
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

            readBites[i] = isItZero(stegoContainer, Channel.BLUE, x, y, q) ? (byte) 0 : (byte) 1;
        }

        return ByteDistributor.collectBitsBy(readBites, 1);
    }


    public static void putIn(BufferedImage stegoContainer, List<Channel> usedChannels, byte[] information, int qInByte)
    {
        if (usedChannels.isEmpty() || information.length == 0)
            return;

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
                    return;
            }
        }
    }


    public static byte[] takeOutInf(BufferedImage stegoContainer, List<Channel> usedChannels, int qInByte, int bytesQuantity)
    {
        if (usedChannels.isEmpty() || bytesQuantity == 0)
            return new byte[0];

        return ByteDistributor.collectBitsBy(readBytes(stegoContainer, usedChannels, qInByte, bytesQuantity), qInByte);
    }


    private static int calcValForCutterMethod(BufferedImage stegoContainer, Color color, double sglEnergy, byte inf)
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


    private static boolean isItZero(BufferedImage image, Channel channel, final int x, final int y, int q)
    {
        int channelVal = ColorUtils.getChannelVal(image.getRGB(x, y), channel);
        double avgChannelVal = ColorUtils.calcAvgChannelVal(image, channel, x, y, q);

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
        int bitmask  = Byte.toUnsignedInt(Bitmask.fromNum(8 - qInByte, true).getMask());

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

        int bitmask  = Bitmask.fromNum(qInByte, false).getMask();

        return switch (ch) {
            case RED -> (byte) (pixelColor.getRed() & bitmask);
            case GREEN -> (byte) (pixelColor.getGreen() & bitmask);
            case BLUE -> (byte) (pixelColor.getBlue() & bitmask);
            case ALPHA -> (byte) (pixelColor.getAlpha() & bitmask);
        };
    }
}

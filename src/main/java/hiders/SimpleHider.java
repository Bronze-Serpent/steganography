package hiders;

import utils.Channel;
import utils.ByteBitmask;
import utils.ByteDistributor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;


public class SimpleHider implements Hider
{
    private final List<Channel> usedChannels;

    private final int qInByte;


    public SimpleHider(List<Channel> usedChannels, int qInByte)
    {
        this.usedChannels = usedChannels;
        this.qInByte = qInByte;
    }


    @Override
    public BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf)
    {
        if (usedChannels.isEmpty() || inf.length == 0 || qInByte == 0)
            return stegoContainer;

        byte[] preparedInformation = ByteDistributor.distributeBitsBy(inf, qInByte);
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


    @Override
    public byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity)
    {
        if (usedChannels.isEmpty() || bytesQuantity == 0 || qInByte == 0)
            return new byte[0];

        return ByteDistributor.collectBitsBy(readBytes(stegoContainer, usedChannels, qInByte, bytesQuantity), qInByte);
    }


    @Override
    public boolean willTheInfFit(BufferedImage stegoContainer, byte[] inf)
    {
        return willTheInfFitInTheCont(stegoContainer, inf, usedChannels, qInByte);
    }


    public static boolean willTheInfFitInTheCont(BufferedImage stegoContainer, byte[] inf, List<Channel> usedChannels, int qInByte)
    {
        return usedChannels.size() * qInByte * stegoContainer.getHeight() * stegoContainer.getWidth() >= inf.length * 8;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;


public class PictureStorekeeper implements GraphicStorekeeper
{

    private final BufferedImage stegoContainer;
    private final ByteDistributor byteDistributor;


    public PictureStorekeeper(BufferedImage stegoContainer, ByteDistributor byteDistributor)
    {
        this.stegoContainer = stegoContainer;
        this.byteDistributor = byteDistributor;
    }

    
    @Override
    public void putIn(List<Channel> usedChannels, byte[] information, int qInByte)
    {
        if (usedChannels.isEmpty() || information.length == 0)
            return;

        byte[] preparedInformation = byteDistributor.distributeBitsBy(information, qInByte);
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


    @Deprecated
    public int[] getAlphaBytes(int qInByte, int bitsQuantity)
    {
        int[] readBytes = new int[(int) (Math.ceil(bitsQuantity * 8.0 / qInByte))];

        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());

        for (int i = 0; i < readBytes.length; i++)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());

            readBytes[i] = new Color(stegoContainer.getRGB(x, y), true).getAlpha();
        }

        return readBytes;
    }


    @Override
    public byte[] takeOutInf(List<Channel> usedChannels, int qInByte, int bitsQuantity)
    {
        return byteDistributor.collectBitsBy(readBytes(usedChannels, qInByte, bitsQuantity), qInByte);
    }


    private byte[] readBytes(List<Channel> usedChannels, int qInByte, int bitsQuantity)
    {
        byte[] readBytes = new byte[(int) (Math.ceil(bitsQuantity * 8.0 / qInByte))];

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
        // but the color channels are 8 bits long so it doesn't matter.
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

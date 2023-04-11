import java.awt.*;
import java.awt.image.BufferedImage;


public class ColorUtils
{

    public static double calcBrightness(Color c)
    {
        return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
    }


    /*
    public static double calcAvgChannelVal(BufferedImage image, Channel channel, final int x, final int y, int q)
    {
        int sumChannels = 0;

        for (int c = 0, currX = x; c < q; c++)
        {
            currX = x - 1;
            if (currX > -1)
                sumChannels += getChannelVal(image.getRGB(currX, y), channel);
            else
                break;
        }

        for (int c = 0, currX = x; c < q; c++)
        {
            currX = x + 1;
            if (currX < image.getWidth())
                sumChannels += getChannelVal(image.getRGB(currX, y), channel);
            else
                break;
        }

        for (int c = 0, currY = y; c < q; c++)
        {
            currY = y - 1;
            if (currY > -1)
                sumChannels += getChannelVal(image.getRGB(x, currY), channel);
            else
                break;
        }

        for (int c = 0, currY = y; c < q; c++)
        {
            currY = y + 1;
            if (currY < image.getHeight())
                sumChannels += getChannelVal(image.getRGB(x, currY), channel);
            else
                break;
        }
        return Math.round(sumChannels / (4.0 * q));
    }
*/


    public static double calcAvgChannelVal(BufferedImage image, Channel channel, final int x, final int y, int q)
    {
        int sumChannels = 0;

        for (int i = 0, currY = y + q; i <= q * 2; i++)
        {
            if (currY != y)
            {
                if (isCoordinateAvailable(image, x, currY))
                    sumChannels += getChannelVal(image.getRGB(x, currY), channel);
            }
            else
                {
                    for (int j = 0, currX = x - q; j <= q * 2; j++)
                    {
                        if (isCoordinateAvailable(image, currX, currY) && currX != x)
                            sumChannels += getChannelVal(image.getRGB(currX, currY), channel);

                        currX++;
                    }
                }
            currY--;
        }

        return Math.round(sumChannels / (4.0 * q));
    }


    public static int getChannelVal(int rgb, Channel channel)
    {
        Color color = new Color(rgb, true);

        return switch (channel) {
            case RED -> color.getRed();
            case GREEN -> color.getGreen();
            case BLUE -> color.getBlue();
            case ALPHA -> color.getAlpha();
        };
    }


    private static boolean isCoordinateAvailable(BufferedImage image, int x, int y)
    {
        return x > -1 && x < image.getWidth() && y > -1 && y < image.getHeight();
    }
}

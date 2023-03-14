import java.awt.image.BufferedImage;


public class Metrics
{

    public static double uNMSE(BufferedImage fst, BufferedImage snd)
    {
        int sumSquaredDiff = 0;
        int sumSquaredFst = 0;

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getWidth(); y++)
            {
                sumSquaredDiff += Math.pow(fst.getRGB(x, y) - snd.getRGB(x, y), 2);
                sumSquaredFst += fst.getRGB(x, y) * fst.getRGB(x, y);
            }

        return (double) sumSquaredDiff / sumSquaredFst;
    }
}

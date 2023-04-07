import java.awt.image.BufferedImage;


public class Metrics
{

    public static double uNMSE(BufferedImage fst, BufferedImage snd)
    {
        return (double) sumSquaredDiff(fst, snd) / sumSquaredFst(fst, snd);
    }


    public static double uSNR(BufferedImage fst, BufferedImage snd)
    {
        return 1 / uNMSE(fst, snd);
    }


    public static double uMSE(BufferedImage fst, BufferedImage snd)
    {
        return (1 / (double) (fst.getWidth() * fst.getHeight())) * sumSquaredDiff(fst, snd);
    }


    public static double uLMSE(BufferedImage fst, BufferedImage snd)
    {
        // в формуле не сказано как считать лапласиан для краевых пикселей.
        int sumSquaredDiffLaplas = 0;
        int sumSquaredFstLaplas = 0;

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
            {
                sumSquaredDiffLaplas += laplasian(fst, x, y) - laplasian(snd, x, y);
                sumSquaredFstLaplas += laplasian(fst, x, y);
            }

        return sumSquaredDiffLaplas / (double) sumSquaredFstLaplas;
    }


    private static int laplasian(BufferedImage img, int x, int y)
    {
        if (x < 1 || y < 1 || x > img.getWidth() - 2 || y > img.getHeight() - 2)
            return img.getRGB(x, y);

        return img.getRGB(x + 1, y) + img.getRGB(x, y + 1) + img.getRGB(x - 1, y) + img.getRGB(x, y - 1)
                - 4 * img.getRGB(x , y);
    }


    private static int sumSquaredFst(BufferedImage fst, BufferedImage snd)
    {
        int sumSquaredFst = 0;

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
                sumSquaredFst += fst.getRGB(x, y) * fst.getRGB(x, y);

        return sumSquaredFst;
    }


    private static int sumSquaredDiff(BufferedImage fst, BufferedImage snd)
    {
        int sumSquaredDiff = 0;

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
                sumSquaredDiff += Math.pow(fst.getRGB(x, y) - snd.getRGB(x, y), 2);

        return sumSquaredDiff;
    }
}

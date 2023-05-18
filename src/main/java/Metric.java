import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;


public class Metric
{

    public static double uNMSE(BufferedImage fst, BufferedImage snd)
    {
        return (double) sumSquaredDiff(fst, snd) / sumSquaredFst(fst, snd);
    }


    public static double uLp(BufferedImage fst, BufferedImage snd)
    {
        int p = 2;
        double sum = 0;

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
                sum += Math.pow(fst.getRGB(x, y) - snd.getRGB(x, y), p);

        return Math.pow(sum / (fst.getWidth() * fst.getHeight()), 1.0 / p);
    }


    public static double uSNR(BufferedImage fst, BufferedImage snd)
    {
        return 1 / uNMSE(fst, snd);
    }


    public static double uMSE(BufferedImage fst, BufferedImage snd)
    {
        return (1 / (double) (fst.getWidth() * fst.getHeight())) * sumSquaredDiff(fst, snd);
    }


    public static int umaxD(BufferedImage fst, BufferedImage snd)
    {
        int max = fst.getRGB(0, 0) - snd.getRGB(0, 0);

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 1; y < fst.getHeight(); y++)
            {
                int deviation = fst.getRGB(x, y) - snd.getRGB(x, y);
                if (deviation > max)
                    max = deviation;
            }

        return max;
    }


    public static double uUQI(BufferedImage fst, BufferedImage snd)
    {
        List<Integer> fstRgb = new LinkedList<>();
        List<Integer> sndRgb = new LinkedList<>();

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
            {
                fstRgb.add(fst.getRGB(x, y));
                sndRgb.add(snd.getRGB(x, y));
            }

        double fstMathMean = mathMean(fstRgb);
        double sndMathMean = mathMean(sndRgb);

        return 4 * crossCorrelation(fst, snd) * fstMathMean * sndMathMean / ((Math.pow(correlation(fst), 2)
                + Math.pow(correlation(snd), 2)) * (Math.pow(fstMathMean, 2) + Math.pow(sndMathMean, 2)));
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


    private static double correlation(BufferedImage fst)
    {
        double sum = 0;
        List<Integer> fstRgb = new LinkedList<>();

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
            {
                fstRgb.add(fst.getRGB(x, y));
            }

        double fstMathMean = mathMean(fstRgb);

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
                sum += (fst.getRGB(x, y) - fstMathMean);

        return sum / (fst.getWidth() * fst.getHeight());
    }


    private static double crossCorrelation(BufferedImage fst, BufferedImage snd)
    {
        double sum = 0;
        List<Integer> fstRgb = new LinkedList<>();
        List<Integer> sndRgb = new LinkedList<>();

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
            {
                fstRgb.add(fst.getRGB(x, y));
                sndRgb.add(snd.getRGB(x, y));
            }

        double fstMathMean = mathMean(fstRgb);
        double sndMathMean = mathMean(sndRgb);

        for (int x = 0; x < fst.getWidth(); x++)
            for (int y = 0; y < fst.getHeight(); y++)
                sum += (fst.getRGB(x, y) - fstMathMean) * (snd.getRGB(x, y) - sndMathMean);

        return sum / (fst.getWidth() * fst.getHeight());
    }


    private static double mathMean(List<Integer> numbers)
    {
        return numbers.stream().mapToInt(i -> i).average().getAsDouble();
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

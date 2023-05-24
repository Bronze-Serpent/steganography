import hiders.*;
import utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static utils.MathUtils.*;


public class Main
{

    public static void main(String[] args) throws IOException, HiderSizeException
    {
        task4();
    }


    static void task4() throws IOException, HiderSizeException
    {
        BufferedImage initImg = ImageIO.read(new File("src/main/resources/1.jpg"));
        BufferedImage img = makeImageCopy(initImg);

        byte[] inf = "This is my text".getBytes();

        HiderFactory hiderFactory = new HiderFactory();
        Hider hider = hiderFactory.createHider(HiderType.KOCHZHAO);

        hider.hideInf(img, inf);
        byte[] readInf = hider.takeOutInf(img, inf.length);

        System.out.println(new String(readInf));
        System.out.println("μmaxD = " + Metric.umaxD(initImg, img));
        System.out.println("μMSE = " + Metric.uMSE(initImg, img));
        System.out.println("μUQI = " + Metric.uUQI(initImg, img));
    }


    private static void t7() throws IOException
    {
        BufferedImage initImg = ImageIO.read(new File("src/main/resources/1.jpg"));

        for (int i = 0; i < initImg.getWidth(); i++)
            for (int j = 0; j < initImg.getHeight(); j++)
                System.out.printf("Color blue: %d  blue: %d\n", new Color(initImg.getRGB(i, j)).getBlue(), (initImg.getRGB(i, j)) & 0xFF);
    }


    private static void t6()
    {
        int[][] blueVal = Stream.generate(() -> Stream.generate(() -> ThreadLocalRandom.current().nextInt(255))
                        .limit(8)
                        .mapToInt(e -> e)
                        .toArray())
                .limit(8)
                .toArray(int[][]::new);

        double[][] dctResult = dct8X8(blueVal);
        double[][] idctResult = idct8X8(dctResult);

        System.out.println("Hi");
    }


    static void t4() throws IOException
    {
        BufferedImage img = ImageIO.read(new File("src/main/resources/img17X10.jpg"));

        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(img.getWidth(), img.getHeight(), 4);
        List<List<Coordinate>> splitByMask = CoordinateDistributor.splitIntoTwoGroupsByMask(wholeBlocks.get(0), 107);
        List<List<Coordinate>> splitByBrightness = ColorUtils.splitIntoTwoBrightnessGroups(img, wholeBlocks.get(0));

        System.out.println(wholeBlocks.size());
    }


    static void t5()
    {
        int[] elements = new int[]{240, 120, 200};
        double[] multipliers = new double[]{0.2, 0.3, 0.5};
        int add = 5;

        int[] addedElem = MathUtils.increaseTheAvgOfElem(elements, multipliers, add);
        System.out.printf("%3d-%3d-%3d      initial: %3d-%3d-%3d      add = %2d\n", addedElem[0], addedElem[1],
                addedElem[2], elements[0], elements[1], elements[2], add);

        elements = new int[]{240, 255, 200};
        addedElem = MathUtils.increaseTheAvgOfElem(elements, multipliers, add);
        System.out.printf("%3d-%3d-%3d      initial: %3d-%3d-%3d      add = %2d\n", addedElem[0], addedElem[1],
                addedElem[2], elements[0], elements[1], elements[2], add);

        elements = new int[]{255, 255, 200};
        addedElem = MathUtils.increaseTheAvgOfElem(elements, multipliers, add);
        System.out.printf("%3d-%3d-%3d      initial: %3d-%3d-%3d      add = %2d\n", addedElem[0], addedElem[1],
                addedElem[2], elements[0], elements[1], elements[2], add);

        elements = new int[]{247, 255, 254};
        addedElem = MathUtils.increaseTheAvgOfElem(elements, multipliers, add);
        System.out.printf("%3d-%3d-%3d      initial: %3d-%3d-%3d      add = %2d\n", addedElem[0], addedElem[1],
                addedElem[2], elements[0], elements[1], elements[2], add);
    }


    static void alphaChannelTest() throws IOException
    {
        BufferedImage image = ImageIO.read(new File("src/main/resources/Saruman.jpg"));

        Color color = new Color(12,12,12, 0);
        image.setRGB(0, 0, color.getRGB());

        System.out.println(Integer.toBinaryString(color.getAlpha()));
        System.out.println(Integer.toBinaryString(image.getRGB(0, 0)));

        System.out.println(new Color(image.getRGB(0, 0), true).getRed());
        System.out.println(new Color(image.getRGB(0, 0), true).getAlpha());
    }


    static void t1()
    {
        byte test = 63;

        System.out.println(test);
        System.out.println(Integer.toBinaryString(Byte.toUnsignedInt(test)));

        for (ByteBitmask mask : ByteBitmask.values())
            System.out.printf("Value equals: %3d for mask %11s mask: %8s\n", mask.apply(test), mask, Integer.toBinaryString(Byte.toUnsignedInt(mask.getMask())));
    }

    static void t3()
    {
        for (byte b = -128; b != 127; b++)
            System.out.printf("num = %4d mask = %8s\n", b, Integer.toBinaryString(Byte.toUnsignedInt(b)));
    }


    private static BufferedImage makeImageCopy(BufferedImage imageToCopy)
    {
        BufferedImage result = new BufferedImage(imageToCopy.getWidth(), imageToCopy.getHeight(), imageToCopy.getType());
        Graphics g = result.getGraphics();
        g.drawImage(imageToCopy, 0, 0, null);
        return result;
    }
}

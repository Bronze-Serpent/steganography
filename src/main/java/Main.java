import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        //t1();
        //alphaChannelTest();
        //task1();
        task2();
    }


    public static void task1() throws IOException
    {
        BufferedImage image = ImageIO.read(new File("src/main/resources/Saruman.jpg"));

        byte[] inf = "This is my text".getBytes();
        int qInByte = 3;

        PictureStorekeeper.putIn(image, List.of(Channel.RED, Channel.GREEN, Channel.BLUE), inf, qInByte);
        System.out.println(new String(PictureStorekeeper.takeOutInf(image, List.of(Channel.RED, Channel.GREEN, Channel.BLUE), qInByte, inf.length)));
    }


    public static void task2() throws IOException
    {
        BufferedImage image = ImageIO.read(new File("src/main/resources/8.jpg"));

        byte[] inf = "This is my text".getBytes();
        int q = 3;
        double sglEnergy = 0.6;

        PictureStorekeeper.putInByCutter(image, inf, sglEnergy);
        byte[] readInf = PictureStorekeeper.takeOutInfByCutter(image, q, inf.length);
        System.out.println(new String(readInf));
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

        for (Bitmask mask : Bitmask.values())
            System.out.printf("Value equals: %3d for mask %11s mask: %8s\n", mask.apply(test), mask, Integer.toBinaryString(Byte.toUnsignedInt(mask.getMask())));
    }

    static void t3()
    {
        for (byte b = -128; b != 127; b++)
            System.out.printf("num = %4d mask = %8s\n", b, Integer.toBinaryString(Byte.toUnsignedInt(b)));
    }
}

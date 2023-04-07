import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        //task1();
        t1();
    }


    public static void task1() throws IOException
    {
        BufferedImage image = ImageIO.read(new File("src/main/resources/Saruman.jpg"));
        ByteDistributor distributor = new SimpleByteDistributor();
        GraphicStorekeeper graphicStorekeeper = new PictureStorekeeper(image, distributor);

        byte[] inf = "This is my text".getBytes();
        int qInByte = 3;

        graphicStorekeeper.putIn(List.of(Channel.ALPHA), inf, qInByte);
        System.out.println(new String(graphicStorekeeper.takeOutInf(List.of(Channel.ALPHA), qInByte, inf.length)));
    }


    static void testAlpha() throws IOException
    {
        BufferedImage image = ImageIO.read(new File("src/main/resources/Saruman.jpg"));
        ByteDistributor distributor = new SimpleByteDistributor();
        PictureStorekeeper p = new PictureStorekeeper(image, distributor);
        GraphicStorekeeper graphicStorekeeper = p;

        byte[] inf = "This is my text".getBytes();
        int qInByte = 1;

        System.out.println(Arrays.toString(p.getAlphaBytes(qInByte, inf.length)));
        graphicStorekeeper.putIn(List.of(Channel.ALPHA), inf, qInByte);
        System.out.println(Arrays.toString(p.getAlphaBytes(qInByte, inf.length)));

        System.out.println(new String(graphicStorekeeper.takeOutInf(List.of(Channel.ALPHA), qInByte, inf.length)));
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

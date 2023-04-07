import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class old_version
{
    private static final int QUANTITY_USED_BITS = 2;

    public static void main(String[] args) throws IOException
    {

        BufferedImage image = ImageIO.read(new File("src/main/resources/Csenia.jpg"));
        BufferedImage newImage = hideInf(image, readBytesFromFile("src/main/resources/text.txt"));

        File outputfile = new File("src/main/resources/completed.jpg");
        ImageIO.write(newImage, "png", outputfile);

        byte[] takenOutInf = combineInf(takeOutInf(newImage, 60));
        System.out.println(new String(takenOutInf));
        System.out.println("Metrics");
        System.out.println("μSNR = " + Metrics.uSNR(image, newImage));
        System.out.println("μMSE = " + Metrics.uMSE(image, newImage));
        System.out.println("μLMSE = " + Metrics.uLMSE(image, newImage));

    }


    static BufferedImage hideInf(BufferedImage image, byte[] information)
    {
        if (!isInfPlacedInTheContainer(image, information))
            System.out.println("With the given settings, the information is not placed in the container.");

        BufferedImage stegoContainer = makeImageCopy(image);
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());
        byte[] preparedBytes = preparedInfBytes(information);

        for (byte preparedByte : preparedBytes)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());
            Color pixelColor = new Color(stegoContainer.getRGB(x, y));

            int b = pixelColor.getAlpha() & 252 | preparedByte;

            stegoContainer.setRGB(x, y, new Color(b, pixelColor.getGreen(), pixelColor.getGreen()).getRGB());
        }

        return stegoContainer;
    }


    static List<Byte> takeOutInf(BufferedImage stegoContainer, int bitsQuantity)
    {
        Random elector = new Random((long) stegoContainer.getHeight() * stegoContainer.getWidth());
        List<Byte> readBytes = new LinkedList<>();

        for (int i = 0; i < bitsQuantity; i++)
        {
            int x = elector.nextInt(stegoContainer.getWidth());
            int y = elector.nextInt(stegoContainer.getHeight());
            Color pixelColor = new Color(stegoContainer.getRGB(x, y));

            byte inf = (byte) (pixelColor.getRed() & 3);
            readBytes.add(inf);
        }

        return readBytes;
    }


    static boolean isInfPlacedInTheContainer(BufferedImage stegoContainer, byte[] information)
    {
        return  stegoContainer.getHeight() * stegoContainer.getWidth() * QUANTITY_USED_BITS > information.length * 8;
    }


    static byte[] preparedInfBytes(byte[] information)
    {
        byte[] preparedBytes = new byte[information.length * 4];
        int i = 0;
        for (byte b : information)
        {
            preparedBytes[i++] = (byte) ((b & 192) >>> 6);
            preparedBytes[i++] = (byte) ((b & 48) >>> 4);
            preparedBytes[i++] = (byte) ((b & 12) >>> 2);
            preparedBytes[i++] = (byte) (b & 3);
        }

        return preparedBytes;
    }


    static byte[] combineInf(List<Byte> bytes)
    {
        byte[] inf = new byte[bytes.size() / 4];

        for (int i = 0; i < inf.length; i++)
            for (int j = 0; j < 4; j++)
            {
                inf[i] = (byte) (inf[i] | bytes.get(i * 4 + j));
                if (j != 3)
                    inf[i] = (byte) (inf[i] << 2);
            }

        return inf;
    }


    private static BufferedImage makeImageCopy(BufferedImage imageToCopy)
    {
        BufferedImage result = new BufferedImage(imageToCopy.getWidth(), imageToCopy.getHeight(), imageToCopy.getType());
        Graphics g = result.getGraphics();
        g.drawImage(imageToCopy, 0, 0, null);
        return result;
    }

    public static byte[] readBytesFromFile(String fileName) throws IOException
    {
        byte[] fileByBytesArr;

        try(FileInputStream inputStream = new FileInputStream(fileName))
        {
            fileByBytesArr = new byte[inputStream.available()];
            int bufferSize = 640_000;

            if (inputStream.available() < bufferSize)
                bufferSize = inputStream.available();
            inputStream.read(fileByBytesArr, 0, bufferSize);
        }
        return fileByBytesArr;
    }

}

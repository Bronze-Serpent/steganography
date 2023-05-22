package hiders;


import utils.*;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static utils.DataStructureUtils.*;


public class KochZhaoHider implements Hider
{

    private static final List<Integer> USE_COEFF = List.of(3, 4);

    private static final Channel USE_CHANNEL = Channel.BLUE;

    private static final int DEFAULT_KEY = 354678;

    private final int BLOCK_SIZE;

    private final int EPS;


    public KochZhaoHider(int blockSize, int eps)
    {
        this.BLOCK_SIZE = blockSize;
        this.EPS = eps;
    }


    @Override
    public BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf) throws HiderSizeException
    {
        return hideInf(stegoContainer, inf, BLOCK_SIZE, EPS, DEFAULT_KEY);
    }


    @Override
    public byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity) throws HiderSizeException
    {
        return takeOutInf(stegoContainer, bytesQuantity, BLOCK_SIZE, DEFAULT_KEY);
    }


    @Override
    public boolean willTheInfFit(BufferedImage stegoContainer, byte[] inf)
    {
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), BLOCK_SIZE);

        return wholeBlocks.size() - 1 > inf.length;
    }


    public static byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity, int blockSize, int key) throws HiderSizeException {
        byte[] readBites = new byte[bytesQuantity * 8];
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), blockSize);


        if (bytesQuantity >= wholeBlocks.size())
            throw new HiderSizeException("An attempt to extract " + bytesQuantity + " bits from a" + (wholeBlocks.size() - 1)
                    + "-bit container for this method", wholeBlocks.size() - 1, bytesQuantity);

        Queue<Integer> readingOrder = createRandomQueue(bytesQuantity * 8, key);

        for (int i = 0; i < readBites.length; i++)
        {
            // TODO: 22.05.2023 why "null" doesn't work here?
            // The warning can be suppressed because the check above is performed and
            // the queue is created with the guaranteed required size.
            @SuppressWarnings("all")
            int[] blockBlueVal = wholeBlocks.get(readingOrder.poll()).stream()
                    .mapToInt(coordinate -> (stegoContainer.getRGB(coordinate.x(), coordinate.y())) & 0xFF)
                    .toArray();

            double[] dctCoeff = twoDimArrToSingle(MathUtils.dct8X8(singleArrayToTwoDimBy8(blockBlueVal)));

            if (Math.abs(dctCoeff[USE_COEFF.get(0)]) > Math.abs(dctCoeff[USE_COEFF.get(1)]))
                readBites[i] = 0;
            else
                readBites[i] = 1;
        }

        return ByteDistributor.collectBitsBy(readBites, 1);
    }


    public static BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf, int blockSize, int eps, int key) throws HiderSizeException
    {
        byte[] preparedInf = ByteDistributor.distributeBitsBy(inf, 1);
        List<List<Coordinate>> wholeBlocks = MathUtils.breakIntoWholeBlocks(stegoContainer.getWidth(),
                stegoContainer.getHeight(), blockSize);

        if (inf.length * 8 >= wholeBlocks.size())
            throw new HiderSizeException("An attempt to hide " + inf.length * 8 + " bits to a" + (wholeBlocks.size() - 1)
                    + "-bit container for this method", wholeBlocks.size() - 1, inf.length * 8);

        Queue<Integer> hidingOrder = createRandomQueue(inf.length * 8, key);

        for (byte b : preparedInf)
        {
            // TODO: 22.05.2023 why "null" doesn't work here?
            // The warning can be suppressed because the check above is performed and
            // the queue is created with the guaranteed required size.
            @SuppressWarnings("all")
            List<Coordinate> block = wholeBlocks.get(hidingOrder.poll());
            int[] blockBlueVal = block.stream()
                    .mapToInt(coordinate -> (stegoContainer.getRGB(coordinate.x(), coordinate.y())) & 0xFF)
                    .toArray();

            double[] dctCoeff = twoDimArrToSingle(MathUtils.dct8X8(singleArrayToTwoDimBy8(blockBlueVal)));

            if (b == 0)
            {
                if (Math.abs(dctCoeff[USE_COEFF.get(0)]) - Math.abs(dctCoeff[USE_COEFF.get(1)]) < eps) {
                    double newVal = Math.abs(dctCoeff[USE_COEFF.get(1)]) + eps;
                    dctCoeff[USE_COEFF.get(0)] = dctCoeff[USE_COEFF.get(0)] >= 0 ? newVal : -1 * newVal;
                }
            }
            else
            {
                if (Math.abs(dctCoeff[USE_COEFF.get(0)]) - Math.abs(dctCoeff[USE_COEFF.get(1)]) > -eps) {
                    double newVal = Math.abs(dctCoeff[USE_COEFF.get(0)]) + eps;
                    dctCoeff[USE_COEFF.get(1)] = dctCoeff[USE_COEFF.get(1)] >= 0 ? newVal : -1 * newVal;
                }
            }
            int[] changedBlue = MathUtils.roundToBorders(0, 255,
                    twoDimArrToSingle(MathUtils.idct8X8(singleArrayToTwoDimBy8(dctCoeff))));
            ColorUtils.writeNewColors(stegoContainer, block, Arrays.stream(changedBlue).boxed().toList(), USE_CHANNEL);
        }

        return stegoContainer;
    }
}

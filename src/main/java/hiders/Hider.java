package hiders;

import java.awt.image.BufferedImage;


public interface Hider
{
    BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf) throws HiderSizeException;

    byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity) throws HiderSizeException;

    boolean willTheInfFit(BufferedImage stegoContainer, byte[] inf);
}

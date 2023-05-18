package hiders;

import java.awt.image.BufferedImage;


public interface Hider
{
    BufferedImage hideInf(BufferedImage stegoContainer, byte[] inf);

    byte[] takeOutInf(BufferedImage stegoContainer, int bytesQuantity);

    boolean willTheInfFit(BufferedImage stegoContainer, byte[] inf);
}

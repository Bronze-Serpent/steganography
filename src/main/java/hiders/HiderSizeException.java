package hiders;

// This exception serves to indicate a situation in which the size of the information that is embedded or retrieved
// by this method is greater than the size of the container for this method.
public class HiderSizeException extends Exception
{
    private final int contSize;
    private final int infSize;

    public HiderSizeException(String msg, int contSize, int infSize)
    {
        super(msg);

        this.contSize = contSize;
        this.infSize = infSize;
    }

    public int getContSize() { return contSize; }

    public int getInfSize() { return infSize; }
}

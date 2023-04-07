

public interface ByteDistributor
{

    byte[] distributeBitsBy(byte[] originalArr, int qInByte);

    byte[] collectBitsBy(byte[] originalArr, int qInByte);
}

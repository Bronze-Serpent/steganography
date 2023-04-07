import java.util.List;


public interface GraphicStorekeeper
{

    void putIn(List<Channel> usedChannels, byte[] information, int qInByte);

    byte[] takeOutInf(List<Channel> usedChannels, int qInByte, int bitsQuantity);
}

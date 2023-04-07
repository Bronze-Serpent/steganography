import java.io.File;


public interface DataWorker
{

    byte[] read(File file);

    void write(File file);
}

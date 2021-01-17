package test.file;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class ReadFileNio {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;
        int nbReaded = 0;
        double add = 0D;
        File file = new File("S01_data_sync.bin");
        try (
                FileInputStream fin = new FileInputStream(file);
        ) {
            FileChannel channel = fin.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(nbElems * 16);

            channel.read(byteBuffer);
            byteBuffer.flip();
            int limit = byteBuffer.limit();
            while(limit>0)
            {
                long l = byteBuffer.getLong();
                double v = byteBuffer.getDouble();
                if (l % nbSecADay == 0) {
                    long nbDay = l / nbSecADay;
                    LOG.info("I handle the {} day", nbDay);
                }
                limit-=16;
                add += v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        long end = System.currentTimeMillis();
        LOG.info("Readed in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
        LOG.info("Readed {} ts", nbReaded);
        LOG.info("File is {} bytes. {} octets / elements", file.length(), "" + (1.0 * file.length() / nbElems ) );
    }

}

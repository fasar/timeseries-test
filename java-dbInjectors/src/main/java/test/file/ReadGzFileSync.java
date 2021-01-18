package test.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.zip.GZIPInputStream;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class ReadGzFileSync {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;
        int nbReaded = 0;
        double add = 0D;
        File file = new File("S01_data_sync.bin.gz");
        try (
                FileInputStream fin = new FileInputStream(file);
                BufferedInputStream bin = new BufferedInputStream(fin, nbElems * 16);
                GZIPInputStream gin = new GZIPInputStream(bin);
                DataInputStream din = new DataInputStream(gin)
        ) {
            long length = file.length();
            while (length > 0) {
                long l = din.readLong();
                double v = din.readDouble();
                if (l % nbSecADay == 0) {
                    long nbDay = l / nbSecADay;
                    LOG.info("I handle the {} day", nbDay);
                }
                nbReaded++;
                add += v;
                length -= 16;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        long end = System.currentTimeMillis();
        LOG.info("Read in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
        LOG.info("Read {} ts", nbReaded);
        LOG.info("File is {} bytes. {} octets / elements", file.length(), "" + (1.0 * file.length() / nbElems ) );

    }

}

package test.file;

import test.Math.SinFonction;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class ReadFileSync {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;
        int nbReaded = 0;
        double add = 0D;
        try (
                FileInputStream fin = new FileInputStream("S01_data_sync.bin");
                BufferedInputStream bin = new BufferedInputStream(fin, nbElems * 16);
                DataInputStream din = new DataInputStream(bin)
        ) {
            while (din.available() > 0) {
                long l = din.readLong();
                double v = din.readDouble();
                if (l % nbSecADay == 0) {
                    long nbDay = l / nbSecADay;
                    LOG.info("I handle the {} day", nbDay);
                }
                nbReaded++;
                add += v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        long end = System.currentTimeMillis();
        LOG.info("Readed in {}", Duration.ofMillis(end - start));
        LOG.info("Readed {} ts", nbReaded);

    }

}

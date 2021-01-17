package test.file;

import test.Math.SinFonction;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class CreateFileSync {
    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;
        try (
                FileOutputStream fout = new FileOutputStream("S01_data_sync.bin");
                BufferedOutputStream bout = new BufferedOutputStream(fout);
                DataOutputStream dos = new DataOutputStream(bout)
        ) {

            for (long i = 0; i < nbElems; i++) {
                if (i % nbSecADay == 0) {
                    long nbDay = i / nbSecADay;
                    LOG.info("I handle the {} day", nbDay);
                }
                double sin = sinFonction.sin(i);
                dos.writeLong(i);
                dos.writeDouble(sin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        long end = System.currentTimeMillis();
        LOG.info("Insertion in {}", Duration.ofMillis(end - start));
        File file = new File("S01_data_sync.bin");
        LOG.info("Output file is {} bytes", file.length());

    }

}

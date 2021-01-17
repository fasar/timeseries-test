package test.file;

import test.Math.SinFonction;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class CreateFileNio {
    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;
        File file = new File("S02_data_sync.bin");
        try (
                FileOutputStream fout = new FileOutputStream(file);
        ) {
            FileChannel channel = fout.getChannel();
            ByteBuffer allocate = ByteBuffer.allocate(nbElems *  16);

            for (long i = 0; i < nbElems; i++) {
                if (i % nbSecADay == 0) {
                    long nbDay = i / nbSecADay;
                    LOG.info("I handle the {} day", nbDay);
                }
                double sin = sinFonction.sin(i);
                allocate.putLong(i);
                allocate.putDouble(sin);
            }
            allocate.flip();
            channel.write(allocate);
            channel.force(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        long end = System.currentTimeMillis();
        LOG.info("Insertion in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
        LOG.info("File is {} bytes. {} octets / elements", file.length(), "" + (1.0 * file.length() / nbElems ) );

    }
}

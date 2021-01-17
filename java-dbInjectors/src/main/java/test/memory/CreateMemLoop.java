package test.memory;

import test.Math.SinFonction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class CreateMemLoop {
    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;
        List<Double> res = new ArrayList<>(nbElems * 2);
        for (int i = 0; i < nbElems; i++) {
            if (i % nbSecADay == 0) {
                int nbDay = i / nbSecADay;
                LOG.info("I handle the {} day", nbDay);
            }
            double sin = sinFonction.sin(i);
            res.add(sin);
            //LOG.trace("Sin is : {}", sin);
        }

        long end = System.currentTimeMillis();
        LOG.info("Insertion in {}", Duration.ofMillis(end - start));
        LOG.info("There is {} elements", res.size());


    }

}

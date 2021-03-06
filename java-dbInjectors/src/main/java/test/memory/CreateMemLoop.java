package test.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Math.SinFonction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class CreateMemLoop {
    private static Logger LOG = LoggerFactory.getLogger("Log");

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
        }

        long end = System.currentTimeMillis();
        LOG.info("Insertion in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));

    }

}

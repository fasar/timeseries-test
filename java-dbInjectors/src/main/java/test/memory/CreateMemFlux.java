package test.memory;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import test.Math.SinFonction;

import java.time.Duration;
import java.util.ArrayList;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class CreateMemFlux {
    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        long start = System.currentTimeMillis();
        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60;

        ArrayList<Double> seed = new ArrayList<>(nbElems);
        Disposable subscribe = Observable
                .rangeLong(0L, 365 * 24 * 60 * 60)
                .doOnNext(i -> {
                    if (i % nbSecADay == 0) {
                        long nbDay = i / nbSecADay;
                        LOG.info("I handle the {} day", nbDay);
                    }
                })
                .doOnDispose(() -> LOG.info("Dispose1"))
                .doOnError(e -> LOG.error(e.getMessage(), e))
                .doOnComplete(() -> {
                    LOG.info("Complete1");
                })
                .reduce(
                        seed,
                        (array, i) -> {
                            double sin = sinFonction.sin(i);
                            array.add(sin);
                            return array;
                        }
                ).subscribe();


        long end = System.currentTimeMillis();
        LOG.info("Insertion in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
        LOG.info("Created {} elements", seed.size());


    }

}

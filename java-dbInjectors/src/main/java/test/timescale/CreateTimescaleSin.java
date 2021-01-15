package test.timescale;

import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Math.SinFonction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateTimescaleSin {
    private static Logger LOG = LoggerFactory.getLogger(CreateTimescaleSin2.class);

    public static void main(String[] args) throws InterruptedException {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/scms_test", "sartor", "Tho2vo3+")) {
            // "0" means disabling the timeout, when doing isValid checks
            boolean isValid = conn.isValid(0);
            System.out.println("Do we have a valid db connection? = " + isValid);
            if (!isValid) {
                throw new Exception("Not connected");
            }
            conn.setAutoCommit(false);
            PreparedStatement insert = conn.prepareStatement("INSERT INTO sensor (ilabel, ts, val) VALUES(?, ?, ?) ON CONFLICT DO NOTHING;");
            long start = System.currentTimeMillis();

            AtomicInteger nb = new AtomicInteger(0);
            int nbSecADay = 24 * 3600;

            Observable
                    .rangeLong(0L, 365 * 24 * 60 * 60)
                    //Observable.<Long, Long>generate(() -> 0L, (a,b) -> {  b.onNext(a + 1); return a+1;})
                    .doOnNext(i -> {
                        int i1 = nb.incrementAndGet();
                        if (i1 % nbSecADay == 0) {
                            int nbDay = i1 / nbSecADay;
                            LOG.info("I handle the {} day", nbDay);
                        }
                    })
                    .window(60*60)

                    .doOnDispose(() -> LOG.info("Dispose1"))
                    .doOnError(e -> LOG.error(e.getMessage(), e))
                    .doOnComplete(() -> LOG.info("Complete1"))
                    .subscribe(windowSeconds -> {
                        windowSeconds
                                .doOnComplete(() -> {
                                    conn.commit();
                                })
                                .doOnSubscribe(e -> {
                                    insert.clearBatch();
                                })
                                .doOnError(e -> LOG.error(e.getMessage(), e))
                                .subscribe(epochSecond -> {
                                    insert.setString(1, "S01_VVLD");
                                    insert.setTimestamp(2, new Timestamp(epochSecond * 1000));
                                    insert.setDouble(3, sinFonction.sin(epochSecond));
                                    insert.addBatch();
                                    insert.execute();
                                }, error -> {
                                    LOG.error("Error 4: {}", error.getMessage(), error);
                                });
                    }, error -> {
                        LOG.error("Error 3: {}", error.getMessage(), error);
                    });

            insert.close();

            long end = System.currentTimeMillis();
            LOG.info("Insertion in {}", Duration.ofMillis(end - start));

            // Do something with the Connection, run some SQL statements
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

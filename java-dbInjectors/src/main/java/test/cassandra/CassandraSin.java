package test.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import io.reactivex.Observable;
import test.Math.SinFonction;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class CassandraSin {
    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);


        CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", 9042))
                .withLocalDatacenter("dc1");
        try (CqlSession session = builder.build()) {
            if (session.isClosed()) {
                LOG.info("Session is closed");
                return;
            }
            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            LOG.info("Cassandra Versions : {}", row.getString("release_version"));

            session.execute(SchemaBuilder.createKeyspace("scms_test").ifNotExists().withSimpleStrategy(1).build());
            session.execute("USE scms_test");

            Statement createTableValue = SchemaBuilder
                    .createTable("tag")
                    .ifNotExists()
                    .withPartitionKey("name", DataTypes.TEXT)
                    .withClusteringColumn("ts", DataTypes.TIMESTAMP)
                    .withColumn("value", DataTypes.DOUBLE)
                    .withClusteringOrder("ts", ClusteringOrder.DESC)
                    .build();
            session.execute(createTableValue);

            long start = System.currentTimeMillis();
            PreparedStatement prepare = session.prepare("INSERT INTO tag (name, ts, value) VALUES (?, ?, ?)");
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
                        BatchStatementBuilder batch = BatchStatement.builder(DefaultBatchType.UNLOGGED);
                        windowSeconds
                                .doOnComplete(() -> {
                                    ResultSet execute = session.execute(batch.build());
                                })
                                .doOnError(e -> LOG.error(e.getMessage(), e))
                                .subscribe(epochSecond -> {
                                    Instant ts = Instant.ofEpochSecond(epochSecond);
                                    BoundStatement s01_vvld = prepare.bind("S01_VVLD", ts, sinFonction.sin(epochSecond));
                                    batch.addStatement(s01_vvld);
                                }, error -> {
                                    LOG.error("Error 4: {}", error.getMessage(), error);
                                });
                    }, error -> {
                        LOG.error("Error 3: {}", error.getMessage(), error);
                    });


            long end = System.currentTimeMillis();
            LOG.info("Insertion in {}", Duration.ofMillis(end - start));

        }
    }
}

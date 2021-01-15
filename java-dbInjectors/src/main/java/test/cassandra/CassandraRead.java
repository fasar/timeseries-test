package test.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import test.Math.SinFonction;

import java.net.InetSocketAddress;
import java.time.Duration;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class CassandraRead {
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
            LOG.info("Before the execute Query");
            ResultSet execute = session.execute("SELECT name, ts, value FROM tag;");
            LOG.info("After the execute Query");
            int nbSecADay = 24 * 3600;
            int nbReaded = 0;
            for (Row row1 : execute) {
                nbReaded++;
                if (nbReaded % nbSecADay == 0) {
                    LOG.info("Readed {} day", nbReaded / nbSecADay);
                }
            }
            long end = System.currentTimeMillis();
            LOG.info("Readed {} elements", nbReaded);
            LOG.info("Readed in {}", Duration.ofMillis(end - start));

        }
    }
}
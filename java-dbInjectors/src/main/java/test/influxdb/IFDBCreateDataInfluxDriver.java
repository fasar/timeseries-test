package test.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Math.SinFonction;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class IFDBCreateDataInfluxDriver {
    public static final int BATCH_SIZE = 3600*12;
    private static Logger LOG = LoggerFactory.getLogger("LOG");


    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        InfluxDBClient client = InfluxDBClientFactory.create(IFDBConst.HOST, IFDBConst.token.toCharArray());
        int nbElems = 365 * 24 * 60 * 60;
        long start = System.currentTimeMillis();
        List<Point> points = new ArrayList<>(BATCH_SIZE);
        try (WriteApi writeApi = client.getWriteApi()) {
            for (int i = 0; i < nbElems; i++) {
                if (i % (24 * 3600) == 0) {
                    LOG.info("Handle day {}", i / (24 * 3600));
                }
                double val = sinFonction.sin(i);
                Point point = Point
                        .measurement("sensor")
                        .addTag("signal", "VVLD")
                        .addTag("station", "S01")
                        .addField("value", val)
                        .time(Instant.ofEpochMilli(i), WritePrecision.MS);
                points.add(point);
                if (points.size() == BATCH_SIZE) {
                    writeApi.writePoints(IFDBConst.bucket, IFDBConst.org, points);
                    points.clear();
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }

        client.close();
        long end = System.currentTimeMillis();
        LOG.info("Insertion in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));

    }
}

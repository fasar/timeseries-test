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

public class IFDBCreateDataInfluxDriver {
    private static Logger LOG = LoggerFactory.getLogger("LOG");


    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);


        // You can generate a Token from the "Tokens Tab" in the UI
        String token = "kRnp4_WBOcO2FtlI9MVPP8u1AcCwH7cvxdlLOlLrmXmaF4w8izBqYFMdlXcj7CR4vhWG_FtqJVbhAChruh-Osg==";
        String bucket = "fsa";
        String org = "FSA";

        InfluxDBClient client = InfluxDBClientFactory.create("http://172.18.0.2:8086", token.toCharArray());

        long start = System.currentTimeMillis();
        try (WriteApi writeApi = client.getWriteApi()) {

            for (int i = 0; i < 365 * 24 * 60 * 60; i++) {
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

                writeApi.writePoint(bucket, org, point);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }

        client.close();
        long end = System.currentTimeMillis();
        LOG.info("Influx data send in {}", Duration.ofMillis(end - start));

    }
}

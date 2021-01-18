package test.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import io.reactivex.Flowable;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.FlowableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Math.SinFonction;
import test.timescale.CreateTimescaleSin2;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class IFDBCreateDataWebAPI {
    private static Logger LOG = LoggerFactory.getLogger("LOG");

    public static void main(String[] args) throws InterruptedException {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        InfluxDBClient client = InfluxDBClientFactory.create(IFDBConst.HOST, IFDBConst.token.toCharArray(), IFDBConst.bucket, IFDBConst.org);
        int nbElems = 365 * 24 * 60 * 60;
        long start = System.currentTimeMillis();
        try (WriteApi writeApi = client.getWriteApi()) {
            for (int i = 0; i < nbElems; i++) {
                if (i % (24 * 3600) == 0) {
                    LOG.info("Handle day {}", i / (24 * 3600));
                }
                double val = sinFonction.sin(i);
                String measurement = String.format("sensor value=%f", val);
                writeApi.writeMeasurement(WritePrecision.MS, measurement);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }

        long end = System.currentTimeMillis();
        LOG.info("Insertion in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
        client.close();

    }


}

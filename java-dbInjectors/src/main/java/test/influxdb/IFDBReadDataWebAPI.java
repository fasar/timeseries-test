package test.influxdb;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.warp10.WriteCounter;

import java.time.Duration;

public class IFDBReadDataWebAPI {
    private static Logger LOG = LoggerFactory.getLogger("LOG");


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        long start = System.currentTimeMillis();
        WriteCounter writeCounter = new WriteCounter();
        long nbElems = 365 * 24 * 60 * 60;

        WebClient client = WebClient.create(vertx);
        String query = "" +
                "from(bucket: \"fsa\")\n" +
                "  |> range(start: 1970-01-01T00:00:00Z)\n" +
                "  |> limit(n: "+nbElems+")  \n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"sensor\")\n" +
                "";
        client
                .post(IFDBConst.PORT, IFDBConst.IP, "/api/v2/query")
                .putHeader("Authorization", "Token " + IFDBConst.token)
                .putHeader("Accept", "application/csv")
                .putHeader("Content-type", "application/vnd.flux")
                .addQueryParam("org", IFDBConst.org)
                .as(BodyCodec.pipe(writeCounter))
                .sendBuffer(Buffer.buffer(query))
                .onComplete(hr -> {
                    if (hr.failed()) {
                        LOG.error("Error {}", hr.cause().getMessage(), hr.cause());
                    } else if (hr.result().statusCode() ==  200 ) {
                        long end = System.currentTimeMillis();
                        long nbElems2 = writeCounter.getCounter();
                        LOG.info("Job done in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems2 / Duration.ofMillis(end - start).toMillis() * 1000));
                        LOG.info("Readed : {} . Expected : {}", nbElems2, nbElems);
                    } else {
                        LOG.error("Can't get elems {}. Response status : {}", writeCounter.getLastRead(), hr.result().statusCode());
                    }
                    client.close();
                    vertx.close();
                });

    }
}

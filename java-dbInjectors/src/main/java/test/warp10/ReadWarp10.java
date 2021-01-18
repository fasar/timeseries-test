package test.warp10;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Math.SinFonction;

import java.time.Duration;

public class ReadWarp10 {
    private static Logger LOG = LoggerFactory.getLogger("log");

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        long start = System.currentTimeMillis();
        WriteCounter writeCounter = new WriteCounter();

        WebClient client = WebClient.create(vertx);
        HttpRequest<Buffer> get = client
                .get(8080, W10Const.IP, "/api/v0/fetch")
                .putHeader("X-Warp10-Token", "readTokenCI")
                .addQueryParam("selector", "sensor{station=S03,signal=VVLD}")
                .addQueryParam("format", "tsv")
                .addQueryParam("start", "2020-01-01T05:00:00Z")
                .addQueryParam("end", "1970-01-01T00:00:00Z")
                .expect(ResponsePredicate.SC_OK);
        get
                .as(BodyCodec.pipe(writeCounter))
                .send()
                .onSuccess(hr -> LOG.info("Received response with status code {}", hr.statusCode()))
                .onFailure(err -> LOG.error("Something went wrong {}", err.getMessage(), err))
                .onComplete(hr -> {
                    if (hr.succeeded()) {
                        long end = System.currentTimeMillis();
                        LOG.info("Total readed: {} elemtes", writeCounter.getCounter());
                        long nbElems = writeCounter.getCounter();
                        LOG.info("Job done in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
                    }
                    client.close();
                    vertx.close();
                });
    }

}

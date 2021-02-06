package test.warp10;

import io.reactivex.Flowable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
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

public class CreateWarp10_10Sins1Year {
    private static Logger LOG = LoggerFactory.getLogger(CreateTimescaleSin2.class);

    public static void main(String[] args) throws InterruptedException {

        int nbSecADay = 24 * 3600;
        int nbElems = 365 * 24 * 60 * 60 * 10;

        Vertx vertx = Vertx.vertx();
        Future<Object> s01 = vertx.executeBlocking(promise -> {
            SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);
            createSignal(vertx, sinFonction, nbSecADay, 0, nbElems, "S01", promise);
        });

        s01.onComplete(hr -> {
            System.out.println("End !!!");
            vertx.close();
        });
    }

    private static void createSignal(Vertx vertx, SinFonction sinFonction, int nbSecADay, long start, int nbElems, String stationName, Promise<Object> promise) {
        AtomicInteger nb = new AtomicInteger(0);
        Flowable<Buffer> data = Flowable
                .rangeLong(start, nbElems)
                .doOnNext(i -> {
                    int i1 = nb.incrementAndGet();
                    if (i1 % nbSecADay == 0) {
                        int nbDay = i1 / nbSecADay;
                        LOG.info("{} I handle the {} day", stationName, nbDay);
                    }
                })
                .map(i -> {
                    double val = sinFonction.sin(i);
                    long nbUs = i * 1000 * 1000;
                    String string = String.format("%d// sensor{station=%s,signal=VVLD} %f%n", nbUs, stationName, val);
                    return string;
                })
                .window(3600)
                .flatMap(f -> {
                    return f.reduce(Buffer.buffer(3600 * 100), (buffer, line) -> buffer.appendString(line)).toFlowable();
                });

        ReadStream<Buffer> bufferStream = FlowableHelper.toReadStream(data);

        long start = System.currentTimeMillis();
        WebClient client = WebClient.create(vertx);
        HttpRequest<Buffer> post = client
                .post(8080, W10Const.IP, "/api/v0/update");

        Future<HttpResponse<Buffer>> future = post
                .putHeader("Transfer-Encoding", "chunked")
                .putHeader("X-Warp10-Token", "writeTokenCI")
                .putHeader("Content-Type", "plain/text")
                .sendStream(bufferStream)
                .onComplete(hr -> {
                    if (hr.failed()) {
                        LOG.error("Error with: {}", hr.cause().getMessage());
                    } else {
                        long end = System.currentTimeMillis();
                        LOG.info("Job {} done in {} = {} elements / seconds", stationName, Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));
                    }
                    client.close();
                    promise.complete();
                });
    }

    public static void deleteSignal(Vertx vertx, Instant start, Instant end, Promise<Object> promise) {
        WebClient client = WebClient.create(vertx);
        client
                .post(8080, W10Const.IP, "/api/v0/delete")
                .putHeader("X-Warp10-Token", "writeTokenCI")
                .addQueryParam("selector", "sensor")
                .addQueryParam("start",start.toString())
                .addQueryParam("end",end.toString())
                .expect(ResponsePredicate.SC_ACCEPTED)
                .send()
                .onComplete(hr -> {
                    if (hr.failed()) {
                        promise.fail(hr.cause());
                    } else {
                        promise.complete();
                    }
                })
                ;
    }

}

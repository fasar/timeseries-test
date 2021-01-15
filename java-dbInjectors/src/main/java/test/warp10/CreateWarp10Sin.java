package test.warp10;

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
import java.util.concurrent.atomic.AtomicInteger;

public class CreateWarp10Sin {
    private static Logger LOG = LoggerFactory.getLogger(CreateTimescaleSin2.class);

    public static void main(String[] args) throws InterruptedException {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        AtomicInteger nb = new AtomicInteger(0);
        int nbSecADay = 24 * 3600;
        Flowable<Buffer> data = Flowable
                .rangeLong(0L, 365 * 24 * 60 * 60)
                .doOnNext(i -> {
                    int i1 = nb.incrementAndGet();
                    if (i1 % nbSecADay == 0) {
                        int nbDay = i1 / nbSecADay;
                        LOG.info("I handle the {} day", nbDay);
                    }
                })
                .map(i -> {
                    double val = sinFonction.sin(i);
                    long nbUs = i * 1000 * 1000;
                    String string = String.format("%d// sensor{station=S03,signal=VVLD} %f%n", nbUs, val);
                    return string;
                })
                .window(3600)
                .flatMap(f -> {
                    return f.reduce(Buffer.buffer(200 * 150), (buffer, line) -> buffer.appendString(line)).toFlowable();
                });

        ReadStream<Buffer> bufferStream = FlowableHelper.toReadStream(data);

        Vertx vertx = Vertx.vertx();
        long start = System.currentTimeMillis();
        WebClient client = WebClient.create(vertx);
        HttpRequest<Buffer> post = client
                .post(8080, "10.10.0.103", "/api/v0/update");

        post
                .putHeader("X-Warp10-Token", "writeTokenCI")
                .putHeader("Content-Type", "application/x-www-form-urlencoded")
                .putHeader("Accept", "*/*")
                .expect(ResponsePredicate.SC_OK)
                .send()
                .flatMap(hr2 -> {
                    Future<HttpResponse<Buffer>> future = post
                            .putHeader("Transfer-Encoding", "chunked")
                            .putHeader("X-Warp10-Token", "writeTokenCI")
                            .putHeader("Content-Type", "plain/text")
                            .sendStream(bufferStream)
                            .onFailure(err -> LOG.error("Error {}", err.getMessage(), err))
                            .onSuccess(hr -> {
                                String body = hr.bodyAsString();
                                LOG.info("Result2 {}", hr.statusCode());
                                LOG.info("End of sending data with : {}", body);
                            });
                    return future;
                })
                .onComplete(hr -> {
                    if (hr.failed()) {
                        LOG.error("Error with: {}", hr.cause().getMessage());
                    } else {
                        long end = System.currentTimeMillis();
                        LOG.info("Insertion in {}", Duration.ofMillis(end - start));
                    }
                    client.close();
                    vertx.close();
                });
    }

}

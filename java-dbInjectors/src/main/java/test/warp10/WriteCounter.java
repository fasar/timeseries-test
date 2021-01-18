package test.warp10;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteCounter implements WriteStream<Buffer> {
    private static Logger LOG = LoggerFactory.getLogger("log");

    private Handler<Void> handler;
    private Handler<Throwable> exceptionHandler;
    private String lastRead = null;
    private long counter;

    @Override
    public WriteStream<Buffer> exceptionHandler(Handler<Throwable> handler) {
        this.exceptionHandler = handler;
        return this;
    }

    public String getLastRead() {
        return lastRead;
    }

    public long getCounter() {
        return counter;
    }

    @Override
    public Future<Void> write(Buffer data) {
        onWrite(data);
        return Future.succeededFuture();
    }

    private void onWrite(Buffer data) {
        lastRead = data.toString();
        int nbLines = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.getByte(i) == '\n') {
                nbLines++;
            }
        }
        long before = counter % (24 * 3600);
        counter += nbLines;
        long after = counter % (24 * 3600);
        if (after< before) {
            LOG.info("Read day {}",  counter / (24 * 3600));
        }
    }

    @Override
    public void write(Buffer data, Handler<AsyncResult<Void>> handler) {
        onWrite(data);
        handler.handle(Future.succeededFuture());
    }

    @Override
    public void end(Handler<AsyncResult<Void>> handler) {
        handler.handle(Future.succeededFuture());
    }

    @Override
    public WriteStream<Buffer> setWriteQueueMaxSize(int maxSize) {
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return false;
    }

    @Override
    public WriteStream<Buffer> drainHandler(@Nullable Handler<Void> handler) {
        this.handler = handler;
        return this;
    }
}

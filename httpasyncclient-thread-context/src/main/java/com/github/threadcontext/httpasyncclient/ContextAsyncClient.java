package com.github.threadcontext.httpasyncclient;

import com.github.threadcontext.Context;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class ContextAsyncClient extends CloseableHttpAsyncClient {

    private final CloseableHttpAsyncClient client;
    private final Supplier<Context> contextSupplier;

    public ContextAsyncClient(CloseableHttpAsyncClient client, Supplier<Context> contextSupplier) {
        this.client = client;
        this.contextSupplier = contextSupplier;
    }

    public boolean isRunning() {
        return client.isRunning();
    }

    public void start() {
        client.start();
    }

    public void close() throws IOException {
        client.close();
    }

    public <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        Context threadContext = contextSupplier.get();
        return client.execute(
            new ContextAsyncRequestProducer(producer, threadContext),
            new ContextAsyncResponseConsumer<>(responseConsumer, threadContext),
            context,
            new ContextFutureCallback<>(callback, threadContext)
        );
    }
}

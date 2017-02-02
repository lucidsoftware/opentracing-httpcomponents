package io.opentracing.contrib.httpcomponents;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spanmanager.SpanManager.ManagedSpan;
import io.opentracing.tag.Tags;
import java.io.IOException;
import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class SpanHttpAsyncClient extends CloseableHttpAsyncClient {

    private final CloseableHttpAsyncClient client;
    private final SpanManager spanManager;
    private final Tracer tracer;

    public SpanHttpAsyncClient(CloseableHttpAsyncClient client, SpanManager spanManager, Tracer tracer) {
        this.client = client;
        this.spanManager = spanManager;
        this.tracer = tracer;
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
        Span span = tracer.buildSpan("HTTP").addReference(References.CHILD_OF, spanManager.currentSpan().context()).start();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            return client.execute(
                new SpanHttpAsyncRequestProducer(producer, spanManager),
                new SpanHttpAsyncResponseConsumer<>(responseConsumer, spanManager),
                context,
                new SpanFutureCallback<>(callback, spanManager)
            );
        }
    }
}

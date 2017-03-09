package io.opentracing.contrib.httpcomponents;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.threadcontext.ContextSpan;
import java.io.IOException;
import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class SpanHttpAsyncClient extends CloseableHttpAsyncClient {

    private final CloseableHttpAsyncClient client;
    private final Tracer tracer;
    private final ContextSpan contextSpan;

    public SpanHttpAsyncClient(CloseableHttpAsyncClient client, Tracer tracer, ContextSpan contextSpan) {
        this.client = client;
        this.tracer = tracer;
        this.contextSpan = contextSpan;
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
        Span span = tracer.buildSpan("HTTP").addReference(References.CHILD_OF, contextSpan.get().context())
            .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
            .start();
        return contextSpan.set(span).supply(() -> client.execute(producer, responseConsumer, context, callback));
    }

}

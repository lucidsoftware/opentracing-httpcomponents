package io.opentracing.contrib.httpcomponents;

import io.opentracing.noop.NoopSpan;
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
    private final HttpTaggerFactory taggerFactory;

    public SpanHttpAsyncClient(final CloseableHttpAsyncClient client, final Tracer tracer, final ContextSpan contextSpan, final HttpTaggerFactory taggerFactory) {
        this.client = client;
        this.tracer = tracer;
        this.contextSpan = contextSpan;
        this.taggerFactory = taggerFactory;
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
        Span parentSpan = null;
        if (contextSpan.get() != NoopSpan.INSTANCE) {
            parentSpan = contextSpan.get();
        }
        Span span = tracer.buildSpan("HTTP")
            .asChildOf(parentSpan)
            .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
            .start();
        HttpTagger tagger = taggerFactory.create(span, context);
        return contextSpan.set(span).supply(() -> client.execute(
            new SpanHttpAsyncRequestProducer(producer, tracer, span, tagger),
            new SpanHttpAsyncResponseConsumer<>(responseConsumer, span, tagger),
            context,
            new SpanFutureCallback<>(callback, span, tagger)
        ));
    }

}

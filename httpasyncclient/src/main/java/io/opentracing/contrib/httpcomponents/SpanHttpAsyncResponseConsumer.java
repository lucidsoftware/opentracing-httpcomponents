package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spanmanager.SpanManager.ManagedSpan;
import io.opentracing.tag.Tags;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class SpanHttpAsyncResponseConsumer<T> implements HttpAsyncResponseConsumer<T> {

    private final HttpAsyncResponseConsumer<T> delegate;
    private final SpanManager spanManager;
    private final Span span;

    public SpanHttpAsyncResponseConsumer(HttpAsyncResponseConsumer<T> delegate, SpanManager spanManager) {
        this.delegate = delegate;
        this.spanManager = spanManager;
        this.span = spanManager.currentSpan();
    }

    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.responseReceived(response);
            StandardHttpTagger.tagResponse(managedSpan.getSpan(), response);
        }
    }

    public void consumeContent(ContentDecoder decoder, IOControl ioctrl) throws IOException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.consumeContent(decoder, ioctrl);
        }
    }

    public void responseCompleted(HttpContext context) {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.responseCompleted(context);
        }
    }

    public void failed(Exception ex) {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.failed(ex);
            Tags.ERROR.set(managedSpan.getSpan(), true);
        }
    }

    public Exception getException() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            return delegate.getException();
        }
    }

    public T getResult() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            return delegate.getResult();
        }
    }

    public boolean isDone() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            return delegate.isDone();
        }
    }

    public void close() throws IOException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.close();
        }
    }

    public boolean cancel() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            managedSpan.getSpan().log("cancel");
            return delegate.cancel();
        }
    }
}

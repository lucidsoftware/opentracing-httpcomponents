package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
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
    private final Span span;

    public SpanHttpAsyncResponseConsumer(HttpAsyncResponseConsumer<T> delegate, Span span) {
        this.delegate = delegate;
        this.span = span;
    }

    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        StandardHttpTagger.tagResponse(span, response);
    }

    public void consumeContent(ContentDecoder decoder, IOControl ioctrl) throws IOException {
        delegate.consumeContent(decoder, ioctrl);
    }

    public void responseCompleted(HttpContext context) {
        span.log("responseCompleted");
        delegate.responseCompleted(context);
    }

    public void failed(Exception ex) {
        Tags.ERROR.set(span, true);
        delegate.failed(ex);
    }

    public Exception getException() {
        return delegate.getException();
    }

    public T getResult() {
        return delegate.getResult();
    }

    public boolean isDone() {
        return delegate.isDone();
    }

    public void close() throws IOException {
        delegate.close();
    }

    public boolean cancel() {
        span.log("cancel");
        return delegate.cancel();
    }

}

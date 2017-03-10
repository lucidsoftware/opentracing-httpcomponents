package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apache.http.concurrent.FutureCallback;

public class SpanFutureCallback<T> implements FutureCallback<T> {

    private final FutureCallback<T> delegate;
    private final Span span;

    public SpanFutureCallback(FutureCallback<T> delegate, Span span) {
        this.delegate = delegate;
        this.span = span;
    }

    public void completed(T result) {
        span.finish();
        delegate.completed(result);
    }

    public void failed(Exception ex) {
        span.finish();
        Tags.ERROR.set(span, true);
        delegate.failed(ex);
    }

    public void cancelled() {
        span.finish();
        Tags.ERROR.set(span, true);
        span.log("client cancel");
    }

}

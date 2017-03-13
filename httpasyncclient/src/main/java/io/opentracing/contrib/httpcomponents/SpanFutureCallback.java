package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apache.http.concurrent.FutureCallback;

public class SpanFutureCallback<T> implements FutureCallback<T> {

    private final FutureCallback<T> delegate;
    private final Span span;
    private final HttpTagger tagger;

    public SpanFutureCallback(final FutureCallback<T> delegate, final Span span, final HttpTagger tagger) {
        this.delegate = delegate;
        this.span = span;
        this.tagger = tagger;
    }

    public void completed(T result) {
        tagger.tagContext();
        span.finish();
        if (delegate != null) {
            delegate.completed(result);
        }
    }

    public void failed(Exception ex) {
        Tags.ERROR.set(span, true);
        span.finish();
        if (delegate != null) {
            delegate.failed(ex);
        }
    }

    public void cancelled() {
        Tags.ERROR.set(span, true);
        span.log("client cancel");
        span.finish();
        if (delegate != null) {
            delegate.cancelled();
        }
    }

}

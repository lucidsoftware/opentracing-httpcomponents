package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spanmanager.SpanManager.ManagedSpan;
import org.apache.http.concurrent.FutureCallback;

public class SpanFutureCallback<T> implements FutureCallback<T> {

    private final FutureCallback<T> delegate;
    private final SpanManager spanManager;
    private final Span span;

    public SpanFutureCallback(FutureCallback<T> delegate, SpanManager spanManager) {
        this.delegate = delegate;
        this.spanManager = spanManager;
        this.span = spanManager.currentSpan();
    }

    public void completed(T result) {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.completed(result);
        }
    }

    public void failed(Exception ex) {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.failed(ex);
        }
    }

    public void cancelled() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.cancelled();
        }
    }

}

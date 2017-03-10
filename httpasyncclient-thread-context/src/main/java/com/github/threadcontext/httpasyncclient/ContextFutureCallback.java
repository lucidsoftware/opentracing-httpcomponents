package com.github.threadcontext.httpasyncclient;

import com.github.threadcontext.Context;
import org.apache.http.concurrent.FutureCallback;

public class ContextFutureCallback<T> implements FutureCallback<T> {

    private final FutureCallback<T> delegate;
    private final Context context;

    public ContextFutureCallback(FutureCallback<T> delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    public void completed(T result) {
        context.run(() -> delegate.completed(result));
    }

    public void failed(Exception ex) {
        context.run(() -> delegate.failed(ex));
    }

    public void cancelled() {
        context.run(delegate::cancelled);
    }

}

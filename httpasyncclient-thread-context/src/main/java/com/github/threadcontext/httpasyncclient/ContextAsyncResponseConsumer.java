package com.github.threadcontext.httpasyncclient;

import com.github.threadcontext.Context;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class ContextAsyncResponseConsumer<T> implements HttpAsyncResponseConsumer<T> {

    private final HttpAsyncResponseConsumer<T> delegate;
    private final Context context;

    public ContextAsyncResponseConsumer(HttpAsyncResponseConsumer<T> delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        context.<Void, IOException, HttpException>supplyException2(() -> {
            delegate.responseReceived(response);
            return null;
        });
    }

    public void consumeContent(ContentDecoder decoder, IOControl ioctrl) throws IOException {
        context.supplyException1(() -> {
            delegate.consumeContent(decoder, ioctrl);
            return null;
        });
    }

    public void responseCompleted(HttpContext context) {
        this.context.run(() -> delegate.responseCompleted(context));
    }

    public void failed(Exception ex) {
        context.run(() -> delegate.failed(ex));
    }

    public Exception getException() {
        return context.supply(delegate::getException);
    }

    public T getResult() {
        return context.supply(delegate::getResult);
    }

    public boolean isDone() {
        return context.supply(delegate::isDone);
    }

    public void close() throws IOException {
        context.supplyException1(() -> {
            delegate.close();
            return null;
        });
    }

    public boolean cancel() {
        return context.supply(delegate::cancel);
    }
}

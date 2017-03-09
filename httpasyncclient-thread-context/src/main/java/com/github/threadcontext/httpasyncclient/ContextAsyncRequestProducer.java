package com.github.threadcontext.httpasyncclient;

import com.github.threadcontext.Context;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

public class ContextAsyncRequestProducer implements HttpAsyncRequestProducer {
    
    private final HttpAsyncRequestProducer delegate;
    private final Context context;

    public ContextAsyncRequestProducer(HttpAsyncRequestProducer delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    public HttpHost getTarget() {
        return context.supply(delegate::getTarget);
    }

    public HttpRequest generateRequest() throws IOException, HttpException {
        return context.<HttpRequest, IOException, HttpException>supplyException2(delegate::generateRequest);
    }

    public void produceContent(ContentEncoder encoder, IOControl ioctrl) throws IOException {
        context.supplyException1(() -> {
            delegate.produceContent(encoder, ioctrl);
            return null;
        });
    }

    public void requestCompleted(HttpContext context) {
        this.context.run(() -> delegate.requestCompleted(context));
    }

    public void failed(Exception ex) {
        context.run(() -> delegate.failed(ex));
    }

    public boolean isRepeatable() {
        return context.supply(delegate::isRepeatable);
    }

    public void resetRequest() throws IOException {
        context.supplyException1(() -> {
            delegate.resetRequest();
            return null;
        });
    }

    public void close() throws IOException {
        context.supplyException1(() -> {
            delegate.close();
            return null;
        });
    }

}

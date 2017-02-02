package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spanmanager.SpanManager.ManagedSpan;
import io.opentracing.tag.Tags;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

public class SpanHttpAsyncRequestProducer implements HttpAsyncRequestProducer {
    
    private final HttpAsyncRequestProducer delegate;
    private final SpanManager spanManager;
    private final Span span;

    public SpanHttpAsyncRequestProducer(HttpAsyncRequestProducer delegate, SpanManager spanManager) {
        this.delegate = delegate;
        this.spanManager = spanManager;
        this.span = spanManager.currentSpan();
    }

    public HttpHost getTarget() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            HttpHost target = delegate.getTarget();
            StandardHttpTagger.tagPeer(span, target);
            return target;
        }
    }

    public HttpRequest generateRequest() throws IOException, HttpException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            HttpRequest request = delegate.generateRequest();
            StandardHttpTagger.tagRequest(span, request);
            return request;
        }
    }

    public void produceContent(ContentEncoder encoder, IOControl ioctrl) throws IOException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.produceContent(encoder, ioctrl);
        }
    }

    public void requestCompleted(HttpContext context) {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.requestCompleted(context);
        }
    }

    public void failed(Exception ex) {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.failed(ex);
            Tags.ERROR.set(managedSpan.getSpan(), true);
        }
    }

    public boolean isRepeatable() {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            return delegate.isRepeatable();
        }
    }

    public void resetRequest() throws IOException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.resetRequest();
        }
    }

    public void close() throws IOException {
        try(ManagedSpan managedSpan = spanManager.manage(span)) {
            delegate.close();
        }
    }
}

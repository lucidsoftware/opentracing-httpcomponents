package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
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
    private final Span span;

    public SpanHttpAsyncRequestProducer(HttpAsyncRequestProducer delegate, Span span) {
        this.delegate = delegate;
        this.span = span;
    }

    public HttpHost getTarget() {
        HttpHost target = delegate.getTarget();
        StandardHttpTagger.tagPeer(span, target);
        return target;
    }

    public HttpRequest generateRequest() throws IOException, HttpException {
        HttpRequest request = delegate.generateRequest();
        StandardHttpTagger.tagRequest(span, request);
        return request;
    }

    public void produceContent(ContentEncoder encoder, IOControl ioctrl) throws IOException {
        delegate.produceContent(encoder, ioctrl);
    }

    public void requestCompleted(HttpContext context) {
        span.log("requestCompleted");
        delegate.requestCompleted(context);
    }

    public void failed(Exception ex) {
        Tags.ERROR.set(span, true);
        delegate.failed(ex);
    }

    public boolean isRepeatable() {
        return delegate.isRepeatable();
    }

    public void resetRequest() throws IOException {
        delegate.resetRequest();
    }

    public void close() throws IOException {
        delegate.close();
    }

}

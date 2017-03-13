package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
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
    private final Tracer tracer;
    private final Span span;
    private final HttpTagger tagger;

    public SpanHttpAsyncRequestProducer(final HttpAsyncRequestProducer delegate, final Tracer tracer, final Span span, final HttpTagger tagger) {
        this.delegate = delegate;
        this.span = span;
        this.tagger = tagger;
        this.tracer = tracer;
    }

    public HttpHost getTarget() {
        HttpHost target = delegate.getTarget();
        tagger.tagTarget(target);
        return target;
    }

    public HttpRequest generateRequest() throws IOException, HttpException {
        HttpRequest request = delegate.generateRequest();
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpRequestTextMap(request));
        span.setOperationName(String.format("HTTP %s", request.getRequestLine().getMethod()));
        tagger.tagRequest(request);
        return request;
    }

    public void produceContent(ContentEncoder encoder, IOControl ioctrl) throws IOException {
        delegate.produceContent(encoder, ioctrl);
    }

    public void requestCompleted(HttpContext context) {
        span.log("request complete");
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

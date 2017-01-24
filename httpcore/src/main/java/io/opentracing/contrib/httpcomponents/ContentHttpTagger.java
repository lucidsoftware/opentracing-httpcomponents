package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;

public class ContentHttpTagger implements HttpTagger {
    public void tag(Span span, HttpRoute route, HttpRequestWrapper request, HttpClientContext context) {
        HttpRequest original = request.getOriginal();
        if (original instanceof HttpEntityEnclosingRequestBase) {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase)original).getEntity();
            if (entity != null) {
                tagEntity("http.request", span, entity);
            }
        }
    }

    public void tag(Span span, HttpRoute route, HttpResponse response, HttpClientContext context) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            tagEntity("http.response", span, entity);
        }
    }

    private void tagEntity(String prefix, Span span, HttpEntity entity) {
        Header contentType = entity.getContentType();
        if (contentType != null) {
            span.setTag(prefix + ".contentType", contentType.getValue());
        }
        long contentLength = entity.getContentLength();
        if (contentLength >= 0) {
            span.setTag(prefix + ".contentLength", contentLength);
        }
    }
}

package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class ContentHttpTagger implements HttpTagger {

    public static HttpTaggerFactory FACTORY = (span, httpContext) -> new ContentHttpTagger(span);

    private final Span span;

    public ContentHttpTagger(Span span) {
        this.span = span;
    }

    public void tagRequest(HttpRequest request) {
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase)request).getEntity();
            if (entity != null) {
                tagEntity("http.request", entity);
            }
        }
    }

    public void tagResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            tagEntity("http.response", entity);
        }
    }

    private void tagEntity(String prefix, HttpEntity entity) {
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

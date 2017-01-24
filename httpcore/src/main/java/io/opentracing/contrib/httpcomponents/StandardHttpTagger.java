package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

public class StandardHttpTagger implements HttpTagger {
    public void tag(Span span, HttpRequest request, HttpContext context) {
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(span, request.getRequestLine().getMethod());
        Tags.HTTP_URL.set(span, request.getRequestLine().getUri());
        HttpRoute route = (HttpRoute)context.getAttribute(HttpClientContext.HTTP_ROUTE);
        if (route != null) {
            Tags.PEER_HOSTNAME.set(span, route.getTargetHost().getHostName());
            int port = route.getTargetHost().getPort();
            if (port >= 0) {
                Tags.PEER_PORT.set(span, (short) port);
            } else if (route.getTargetHost().getSchemeName().equals("http")) {
                Tags.PEER_PORT.set(span, (short)80);
            } else {
                Tags.PEER_PORT.set(span, (short)443);
            }
        }
    }

    public void tag(Span span, HttpResponse response, HttpContext context) {
        Tags.HTTP_STATUS.set(span, response.getStatusLine().getStatusCode());
    }
}

package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;

interface HttpTagger {
    void tag(Span span, HttpRoute route, HttpRequestWrapper request, HttpClientContext context);

    void tag(Span span, HttpRoute route, HttpResponse response, HttpClientContext context);
}

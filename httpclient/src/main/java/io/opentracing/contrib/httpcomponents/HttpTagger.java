package io.opentracing.contrib.httpcomponents;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface HttpTagger {
    default void tagTarget(HttpHost route) {
    }

    default void tagRequest(HttpRequest request) {
    }

    default void tagResponse(HttpResponse response) {
    }

    default void tagContext() {
    }
}

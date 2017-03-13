package io.opentracing.contrib.httpcomponents;

import org.apache.http.impl.client.HttpClientBuilder;

public final class SpanHttp {

    private SpanHttp() {
    }

    public static HttpClientBuilder trace() {
        return new SpanHttpClientBuilder()
            .addTaggerFactory(StandardHttpTagger.FACTORY)
            .addTaggerFactory(ContentHttpTagger.FACTORY);
    }

}

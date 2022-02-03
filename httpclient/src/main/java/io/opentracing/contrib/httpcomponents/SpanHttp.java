package io.opentracing.contrib.httpcomponents;

import org.apache.http.impl.client.HttpClientBuilder;

public final class SpanHttp {

    private SpanHttp() {
    }

    public static HttpClientBuilder trace() {
        return SpanHttp.trace(NoOpSpanModifier.INSTANCE);
    }

    public static HttpClientBuilder trace(SpanModifier spanModifier) {
        return new SpanHttpClientBuilder()
            .addTaggerFactory(StandardHttpTagger.FACTORY)
            .addTaggerFactory(ContentHttpTagger.FACTORY)
            .setSpanModifier(spanModifier);
    }

}

package io.opentracing.contrib.httpcomponents;

import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import org.apache.http.impl.client.HttpClientBuilder;

public final class SpanHttp {

    private SpanHttp() {
    }

    public static HttpClientBuilder addPropogation(HttpClientBuilder clientBuilder) {
        return clientBuilder.addInterceptorLast(new SpanHttpRequestInterceptor(GlobalTracer.get(), DefaultSpanManager.getInstance()));
    }

}

package io.opentracing.contrib.httpcomponents;

import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;

public final class SpanHttpAsync {

    private SpanHttpAsync() {
    }

    public static HttpAsyncClientBuilder addPropogation(HttpAsyncClientBuilder clientBuilder) {
        SpanManager spanManager = DefaultSpanManager.getInstance();
        return clientBuilder
            .addInterceptorLast(new SpanHttpRequestInterceptor(GlobalTracer.get(), DefaultSpanManager.getInstance()))
            .addInterceptorLast(new SpanContextRequestInterceptor(spanManager, null))
            .setEventHandler(new SpanEventHandler(
                new HttpAsyncRequestExecutor(),
                DefaultSpanManager.getInstance(),
                SpanContextRequestInterceptor.DEFAULT_ATTRIBUTE_KEY
            ));
    }

    public static CloseableHttpAsyncClient createSpans(CloseableHttpAsyncClient client) {
        return new SpanHttpAsyncClient(client, DefaultSpanManager.getInstance(), GlobalTracer.get());
    }

}

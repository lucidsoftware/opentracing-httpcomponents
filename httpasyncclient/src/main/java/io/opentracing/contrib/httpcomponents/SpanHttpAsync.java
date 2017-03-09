package io.opentracing.contrib.httpcomponents;

import com.github.threadcontext.Context;
import com.github.threadcontext.httpasyncclient.ContextAsyncClient;
import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.threadcontext.ContextSpan;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public final class SpanHttpAsync {

    private SpanHttpAsync() {
    }

    public static CloseableHttpAsyncClient trace(HttpAsyncClientBuilder clientBuilder) {
        CloseableHttpAsyncClient client = clientBuilder
            .addInterceptorLast(new SpanHttpRequestInterceptor(GlobalTracer.get(), ContextSpan.DEFAULT))
            .build();
        return new ContextAsyncClient(new SpanHttpAsyncClient(client, GlobalTracer.get(), ContextSpan.DEFAULT), Context.DEFAULT);
    }

}

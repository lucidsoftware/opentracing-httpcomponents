package io.opentracing.contrib.httpcomponents;

import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.propagation.Format;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class SpanHttpRequestInterceptor implements HttpRequestInterceptor {

    private final Tracer tracer;
    private final SpanManager spanManager;

    public SpanHttpRequestInterceptor(Tracer tracer, SpanManager spanManager) {
        this.spanManager = spanManager;
        this.tracer = tracer;
    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        this.tracer.inject(spanManager.currentSpan().context(), Format.Builtin.HTTP_HEADERS, new HttpRequestTextMap(request));
    }
}

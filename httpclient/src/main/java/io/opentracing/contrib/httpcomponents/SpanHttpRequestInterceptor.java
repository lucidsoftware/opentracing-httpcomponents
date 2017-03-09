package io.opentracing.contrib.httpcomponents;

import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.threadcontext.ContextSpan;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class SpanHttpRequestInterceptor implements HttpRequestInterceptor {

    private final Tracer tracer;
    private final ContextSpan contextSpan;

    public SpanHttpRequestInterceptor(Tracer tracer, ContextSpan contextSpan) {
        this.tracer = tracer;
        this.contextSpan = contextSpan;
    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        this.tracer.inject(contextSpan.get().context(), Format.Builtin.HTTP_HEADERS, new HttpRequestTextMap(request));
    }

}

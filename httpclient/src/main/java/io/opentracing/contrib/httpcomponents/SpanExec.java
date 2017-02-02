package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;

public class SpanExec implements ClientExecChain {
    private final ClientExecChain exec;
    private final SpanManager spanManager;
    private final HttpTagger[] taggers;
    private final Tracer tracer;

    public SpanExec(ClientExecChain exec, Tracer tracer, SpanManager spanManager, HttpTagger[]taggers) {
        if (exec == null || taggers == null) {
            throw new IllegalArgumentException();
        }
        this.exec = exec;
        this.spanManager = spanManager;
        this.taggers = taggers;
        this.tracer = tracer;
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Span span = tracer.buildSpan("HTTP " + request.getRequestLine().getMethod())
                .asChildOf(DefaultSpanManager.getInstance().currentSpan())
                .start();
        this.tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpRequestTextMap(request));

        try (Closeable managedSpan = spanManager.manage(span)) {
            for (HttpTagger tagger : taggers) {
                tagger.tag(span, route, request, context);
            }
            CloseableHttpResponse response;
            try {
                 response = exec.execute(route, request, context, execAware);
            } catch (Exception e) {
                Tags.ERROR.set(span, true);
                throw e;
            }
            for (HttpTagger tagger : taggers) {
                tagger.tag(span, route, response, context);
            }
            return response;
        }
    }
}

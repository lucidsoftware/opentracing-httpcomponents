package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.threadcontext.ContextSpan;
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
    private final ContextSpan contextSpan;
    private final HttpTaggerFactory taggerFactory;
    private final Tracer tracer;

    public SpanExec(ClientExecChain exec, Tracer tracer, ContextSpan contextSpan, HttpTaggerFactory taggerFactory) {
        this.exec = exec;
        this.contextSpan = contextSpan;
        this.taggerFactory = taggerFactory;
        this.tracer = tracer;
    }

    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(String.format("HTTP %s", request.getMethod()))
                .asChildOf(contextSpan.get())
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);
                
        Span span = spanBuilder.start();
        this.tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpRequestTextMap(request));
        return contextSpan.set(span).<CloseableHttpResponse, HttpException, IOException>supplyException2(() -> {
            HttpTagger tagger = taggerFactory.create(span, context);
            tagger.tagRequest(request.getOriginal());
            tagger.tagTarget(route.getTargetHost());
            final CloseableHttpResponse response;
            try {
                response = exec.execute(route, request, context, execAware);
            } catch (HttpException | IOException e) {
                Tags.ERROR.set(span, true);
                throw e;
            } finally {
                tagger.tagContext();
            }
            tagger.tagResponse(response);
            return response;
        });        
    }

}

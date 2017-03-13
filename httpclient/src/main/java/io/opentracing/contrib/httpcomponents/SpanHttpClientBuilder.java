package io.opentracing.contrib.httpcomponents;

import io.opentracing.Tracer;
import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.threadcontext.ContextSpan;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;

public class SpanHttpClientBuilder extends HttpClientBuilder {

    private final List<HttpTaggerFactory> taggers;
    private Tracer tracer;
    private ContextSpan contextSpan;
    private String name;

    public SpanHttpClientBuilder() {
        super();
        taggers = new ArrayList<>();
    }

    public SpanHttpClientBuilder addTaggerFactory(HttpTaggerFactory tagger) {
        taggers.add(tagger);
        return this;
    }

    public SpanHttpClientBuilder setSpanManager(ContextSpan contextSpan) {
        this.contextSpan = contextSpan;
        return this;
    }

    public SpanHttpClientBuilder setTracer(Tracer tracer) {
        this.tracer = tracer;
        return this;
    }

    protected ClientExecChain decorateMainExec(ClientExecChain exec) {
        Tracer tracer = this.tracer != null ? this.tracer : GlobalTracer.get();
        ContextSpan contextSpan = this.contextSpan != null ? this.contextSpan : ContextSpan.DEFAULT;
        return new SpanExec(exec, tracer, contextSpan, CombinedHttpTagger.factory(taggers));
    }

}

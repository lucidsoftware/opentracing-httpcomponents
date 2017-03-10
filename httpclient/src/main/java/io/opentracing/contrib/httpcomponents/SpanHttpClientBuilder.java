package io.opentracing.contrib.httpcomponents;

import io.opentracing.Tracer;
import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.threadcontext.ContextSpan;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;

public class SpanHttpClientBuilder extends HttpClientBuilder {

    private HttpTagger[] taggers = new HttpTagger[] { new StandardHttpTagger(), new ContentHttpTagger() };
    private Tracer tracer;
    private ContextSpan contextSpan;
    private String name;

    public SpanHttpClientBuilder setTaggers(HttpTagger[] taggers) {
        if (taggers != null) {
            taggers = taggers.clone();
        }
        this.taggers = taggers;
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
        HttpTagger[] taggers = this.taggers != null ? this.taggers : new HttpTagger[] {};
        return new SpanExec(exec, tracer, contextSpan, taggers);
    }

}

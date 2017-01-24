package io.opentracing.contrib.httpcomponents;

import io.opentracing.Tracer;
import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;

public class SpanHttpClientBuilder extends HttpClientBuilder {

    private HttpTagger[] taggers;
    private Tracer tracer;
    private SpanManager spanManager;

    public SpanHttpClientBuilder setTaggers(HttpTagger[] taggers) {
        if (taggers != null) {
            taggers = taggers.clone();
        }
        this.taggers = taggers;
        return this;
    }

    public SpanHttpClientBuilder setSpanManager(SpanManager spanManager) {
        this.spanManager = spanManager;
        return this;
    }

    public SpanHttpClientBuilder setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    protected ClientExecChain decorateMainExec(ClientExecChain exec) {
        Tracer tracer = this.tracer != null ? this.tracer : GlobalTracer.get();
        SpanManager spanManager = this.spanManager != null ? this.spanManager : DefaultSpanManager.getInstance();
        HttpTagger[] taggers = this.taggers != null ? this.taggers : new HttpTagger[] {};
        return new SpanManagerExec(exec, tracer, spanManager, taggers);
    }

}

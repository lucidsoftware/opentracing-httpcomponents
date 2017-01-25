package io.opentracing.contrib.httpcomponents;

import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import org.apache.http.impl.execchain.ClientExecChain;

public class DefaultSpanManagerExec extends SpanExec {

    public DefaultSpanManagerExec(ClientExecChain exec, HttpTagger[] tagger) {
        super(exec, GlobalTracer.get(), DefaultSpanManager.getInstance(), tagger);
    }

}

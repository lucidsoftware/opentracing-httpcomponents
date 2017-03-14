package io.opentracing.contrib.httpcomponents;

import com.github.threadcontext.Context;
import com.github.threadcontext.httpasyncclient.ContextAsyncClient;
import io.opentracing.contrib.global.GlobalTracer;
import io.opentracing.threadcontext.ContextSpan;
import java.util.Arrays;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

public final class SpanHttpAsync {

    private SpanHttpAsync() {
    }

    public static CloseableHttpAsyncClient trace(CloseableHttpAsyncClient client) {
        final HttpTaggerFactory taggerFactory = CombinedHttpTagger.factory(Arrays.asList(
            StandardHttpTagger.FACTORY,
            ContentHttpTagger.FACTORY
        ));
        return new SpanHttpAsyncClient(new ContextAsyncClient(client, Context.DEFAULT), GlobalTracer.get(), ContextSpan.DEFAULT, taggerFactory);
    }

}

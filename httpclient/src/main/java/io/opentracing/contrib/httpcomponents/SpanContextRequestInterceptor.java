package io.opentracing.contrib.httpcomponents;

import io.opentracing.contrib.spanmanager.SpanManager;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class SpanContextRequestInterceptor implements HttpRequestInterceptor {

    public static String DEFAULT_ATTRIBUTE_KEY = "opentracing.span";

    private final String attributeKey;
    private final SpanManager spanManager;

    public SpanContextRequestInterceptor(SpanManager spanManager, String attributeKey) {
        this.attributeKey = attributeKey != null ? attributeKey : DEFAULT_ATTRIBUTE_KEY;
        this.spanManager = spanManager;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        context.setAttribute(attributeKey, spanManager.currentSpan());
    }
}

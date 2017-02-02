package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.contrib.spanmanager.SpanManager;
import io.opentracing.contrib.spanmanager.SpanManager.ManagedSpan;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.http.HttpException;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.protocol.HttpContext;

/**
 * We have to employ reflection to grab the flow id from the [[HttpClientContext]]. There might be a better way; perhaps
 * mucking with the IOReactors. That's more direct like is done for thread pools but that's also more invasive and
 * probably harder.
 */
public class SpanEventHandler implements NHttpClientEventHandler {
    private Field contextField;
    private final NHttpClientEventHandler delegate;
    private final SpanManager spanManager;
    private final String attributeKey;

    SpanEventHandler(NHttpClientEventHandler delegate, SpanManager spanManager, String attributeKey) {
        this.delegate = delegate;
        this.spanManager = spanManager;
        this.attributeKey = attributeKey;
        Class<?> klass;
        try {
            klass = getClass().getClassLoader().loadClass("org.apache.http.impl.nio.client.AbstractClientExchangeHandler");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            contextField = klass.getDeclaredField("localContext");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        contextField.setAccessible(true);
    }

    @FunctionalInterface
    private interface HttpThunk {
        void get() throws IOException, HttpException;
    }

    /**
     * First get the flow id from the request handler. Since multiple requests can be made per connection, there may not
     * be a request handler. Therefore, store the flow id from the last known request handler in the connection context,
     * and use that when there is no current request handler.
     */
    private void withFlowId(NHttpClientConnection connection, HttpThunk f) throws IOException, HttpException {
        HttpContext connectionContext = connection.getContext();
        Object handler = connectionContext.getAttribute(HttpAsyncRequestExecutor.HTTP_HANDLER);
        Span span = null;
        if (handler == null) {
            span = (Span) connectionContext.getAttribute(attributeKey);
        } else {
            HttpClientContext requestContext;
            try {
                requestContext = (HttpClientContext) contextField.get(handler);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            span = (Span)requestContext.getAttribute(attributeKey);
            if (span == null) {
                span = (Span)connectionContext.removeAttribute(attributeKey);
            } else {
                connectionContext.setAttribute(attributeKey, span);
            }
        }
        if (span == null) {
            f.get();
        }
        try (ManagedSpan managedSpan = spanManager.manage(span)) {
            f.get();
        }
    }

    @Override
    public void connected(NHttpClientConnection conn, Object attachment) throws IOException, HttpException {
        withFlowId(conn, () -> delegate.connected(conn, attachment));
    }

    @Override
    public void requestReady(NHttpClientConnection conn) throws IOException, HttpException {
        withFlowId(conn, () -> delegate.requestReady(conn));
    }

    @Override
    public void responseReceived(NHttpClientConnection conn) throws IOException, HttpException {
        withFlowId(conn, () -> delegate.responseReceived(conn));
    }

    @Override
    public void inputReady(NHttpClientConnection conn, ContentDecoder decoder) throws IOException, HttpException {
        withFlowId(conn, () -> delegate.inputReady(conn, decoder));
    }

    @Override
    public void outputReady(NHttpClientConnection conn, ContentEncoder encoder) throws IOException, HttpException {
        withFlowId(conn, () -> delegate.outputReady(conn, encoder));
    }

    @Override
    public void endOfInput(NHttpClientConnection conn) throws IOException {
        try {
            withFlowId(conn, () -> delegate.endOfInput(conn));
        } catch (HttpException e) {
        }
    }

    @Override
    public void timeout(NHttpClientConnection conn) throws IOException, HttpException {
        withFlowId(conn, () -> delegate.timeout(conn));
    }

    @Override
    public void closed(NHttpClientConnection conn) {
        try {
            withFlowId(conn, () -> delegate.closed(conn));
        } catch (HttpException | IOException e) {
        }
    }

    @Override
    public void exception(NHttpClientConnection conn, Exception ex) {
        try {
            withFlowId(conn, () -> delegate.exception(conn, ex));
        } catch (HttpException | IOException e) {
        }
    }
}

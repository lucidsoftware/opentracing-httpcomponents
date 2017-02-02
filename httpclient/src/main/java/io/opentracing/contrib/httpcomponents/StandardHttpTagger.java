package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;

/**
 * Adds the standard OpenTracing tags defined in {@link Tags}.
 */
public class StandardHttpTagger implements HttpTagger {

    public static void tagPeer(Span span, HttpHost peer) {
        InetAddress address = peer.getAddress();
        if (address instanceof Inet4Address) {
            Tags.PEER_HOST_IPV4.set(span, ByteBuffer.wrap(address.getAddress()).getInt());
        } else if (address instanceof Inet6Address) {
            Tags.PEER_HOST_IPV6.set(span, address.getHostAddress().split("%", 2)[0]);
        }
        Tags.PEER_HOSTNAME.set(span, peer.getHostName());
        int port = peer.getPort();
        if (port >= 0) {
            Tags.PEER_PORT.set(span, (short)port);
        } else if (peer.getSchemeName().equals("http")) {
            Tags.PEER_PORT.set(span, (short)80);
        } else {
            Tags.PEER_PORT.set(span, (short)443);
        }
    }

    public static void tagRequest(Span span, HttpRequest request) {
        Tags.HTTP_METHOD.set(span, request.getRequestLine().getMethod());
        Tags.HTTP_URL.set(span, request.getRequestLine().getUri());
    }

    public static void tagResponse(Span span, HttpResponse response) {
        Tags.HTTP_STATUS.set(span, response.getStatusLine().getStatusCode());
    }

    public void tag(Span span, HttpRoute route, HttpRequestWrapper request, HttpClientContext context) {
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        tagRequest(span, request);
        tagPeer(span, route.getTargetHost());
    }

    public void tag(Span span, HttpRoute route, HttpResponse response, HttpClientContext context) {
        tagResponse(span, response);
    }

}

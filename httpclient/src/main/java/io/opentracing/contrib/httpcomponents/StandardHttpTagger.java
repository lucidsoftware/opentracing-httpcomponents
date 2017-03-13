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

/**
 * Adds the standard OpenTracing tags defined in {@link Tags}.
 */
public class StandardHttpTagger implements HttpTagger {

    public static HttpTaggerFactory FACTORY = (span, httpContext) -> new StandardHttpTagger(span);

    private final Span span;

    public StandardHttpTagger(Span span) {
        this.span = span;
    }

    public void tagTarget(HttpHost peer) {
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

    public void tagRequest(HttpRequest request) {
        Tags.HTTP_METHOD.set(span, request.getRequestLine().getMethod());
        Tags.HTTP_URL.set(span, request.getRequestLine().getUri());
    }

    public void tagResponse(HttpResponse response) {
        Tags.HTTP_STATUS.set(span, response.getStatusLine().getStatusCode());
    }

}

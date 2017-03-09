package io.opentracing.contrib.httpcomponents;

import io.opentracing.propagation.TextMap;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;

public class HttpRequestTextMap implements TextMap {

    private final HttpRequest request;

    public HttpRequestTextMap(HttpRequest request) {
        this.request = request;
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        final HeaderIterator iterator = this.request.headerIterator();
        return new Iterator<Map.Entry<String, String>>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Map.Entry<String, String> next() {
                Header header = iterator.nextHeader();
                return new AbstractMap.SimpleImmutableEntry<>(header.getName(), header.getValue());
            }
        };
    }

    public void put(String key, String value) {
        request.setHeader(key, value);
    }

}

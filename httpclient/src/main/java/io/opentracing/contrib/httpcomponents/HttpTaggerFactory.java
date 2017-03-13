package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;
import org.apache.http.protocol.HttpContext;

public interface HttpTaggerFactory {

    HttpTagger create(Span span, HttpContext context);

}

# OpenTracing for [HttpComponents](https://hc.apache.org/)

[![Build Status](https://travis-ci.com/lucidsoftware/opentracing-httpcomponents.svg?branch=master)](https://travis-ci.com/lucidsoftware/opentracing-playframework)
![Maven Version](https://img.shields.io/maven-central/v/com.lucidchart/opentracing-httpcore.svg)

This library works with [`GlobalTracer`](https://github.com/opentracing-contrib/java-globaltracer) and
[`DefaultSpanManager`](https://github.com/opentracing-contrib/java-spanmanager).

## HttpClient

Install `com.lucidchart:opentracing-httpclient:<version>`

```java
import org.apache.http.impl.client.CloseableHttpClient;
import io.opentracing.contrib.httpcomponents.SpanHttp;
import io.opentracing.threadcontext.ContextSpan;
import io.opentracing.Span;

Span span = ...
CloseableHttpClient client = SpanHttp.trace().build();
ContextSpan.set(span).supplyException2(() -> {
  try {
    client.execute(new HttpGet("http://example.org/"))
  } catch (IOException | ClientProtocolException e) {
  }
})
```

If you are already using a custom subclass of `HttpClientBuilder`, override the `decorateMainExec` of the class.

```java
import org.apache.http.impl.execchain.*;
import io.opentracing.contrib.httpcomponents.*;

new MyHttpClientBuilder {
    protected ClientExecChain decorateMainExec(ClientExecChain exec) {
        new DefaultSpanManagerExec(super.decorateMainExec(exec), new HttpTagger[] { new StandardHttpTagger(); }
    }
}
```

## HttpAsyncClient

Install `com.lucidchart:opentracing-httpasyncclient:<version>`.

```java
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import io.opentracing.contrib.httpcomponents.SpanHttpAsync;
import io.opentracing.threadcontext.ContentSpan;
import io.opentracing.Span;

Span span = ...
CloseableHttpAyncClient client = SpanHttpAsync.trace(HttpAsyncClients.createDefault());
ContextSpan.set(span).supplyException2(() -> {
  try {
    client.execute(new HttpGet("http://example.org/"), null)
  } catch (IOException | ClientProtocolException e) {
  }
})
```

### Taggers

Tags for client `Span`s come from `io.opentracing.contrib.httpcomponents.HttpTagger` instances.

Built-in taggers:

* `StandardHttpTagger`
* `ContentHttpTagger`

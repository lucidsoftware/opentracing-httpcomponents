# OpenTracing for [HttpComponents](https://hc.apache.org/)

[![Build Status](https://travis-ci.org/lucidsoftware/opentracing-httpcomponents.svg?branch=master)](https://travis-ci.org/lucidsoftware/opentracing-playframework)

This library works with [`GlobalTracer`](https://github.com/opentracing-contrib/java-globaltracer) and
[`DefaultSpanManager`](https://github.com/opentracing-contrib/java-spanmanager).

## Install

Add these dependencies as appropriate:

* `com.lucidchart:opentracing-httpclient:<version>`
* `com.lucidchart:opentracing-httpasyncclient:<version>`

## Propogating spans in requests

To propogate spans across the HTTP request, call `SpanHttp.addPropogation` on the `HttpClientBuilder`.

```java
import org.apache.http.impl.*;
import io.opentracing.contrib.httpcomponents.*;

HttpClientBuilder builder = HttpClients.custom();
SpanHttp.addPropogation(builder);
CloseableHttpClient client = builder.build();
```

For the async client,

```java
import org.apache.http.impl.nio.client.*;
import io.opentracing.contrib.httpcomponents.*;

HttpAsyncClientBuilder builder = HttpAsyncClients.custom();
SpanHttpAsync.addPropogation(builder);
CloseableHttpAsyncClient client = builder.build();
```

## Creating spans for requests

To create a new span for each request, replaces uses of `HttpClients` or `HttpClientBuilder` with `SpanHttpClientBuilder`.

```java
import io.opentracing.contrib.httpcomponents.*;

new SpanHttpClientBuilder()
    .setTracer(...)       // defaults to GlobalTracer.get()
    .setSpanManager(...)  // defaults to DefaultSpanManager.get()
    .setTaggers(...)      // defaults to new HttpTagger[] { new StandardHttpTagger(); }
    ...                   // additional client configuration
    .build();
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

### Taggers

Tags for client `Span`s come from `io.opentracing.contrib.httpcomponents.HttpTagger` instances.

Built-in taggers:

* `StandardHttpTagger`
* `ContentHttpTagger`

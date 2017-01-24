# OpenTracing for [HttpComponents](https://hc.apache.org/)

## Propogating spans in requests

To propogate spans across the HTTP request, call `SpanHttp.addPropogation` on the `HttpClientBuilder`.

```java
HttpClientBuilder builder = HttpsClients.custom();
SpanHttp.addPropogation(builder);
CloseableHttpClient client = builder.build();
```

## Creating spans for requests

To create a new span for each request, replaces uses of `HttpClientBuilder` with `SpanHttpClientBuilder`.

```java
new SpanHttpClientBuilder()
    .setTracer(...)       // defaults to GlobalTracer.get()
    .setSpanManager(...)  // defaults to DefaultSpanManager.get()
    .setTaggers(...)      // defaults to new HttpTagger[] { new StandardHttpTagger(); }
    ...
    .build();
```

If using a custom builder, override the `decorateMainExec`.

```java
new MyHttpClientBuilder {
    protected ClientExecChain decorateMainExec(ClientExecChain exec) {
        new DefaultSpanManagerExec(exec, new HttpTagger[] { new StandardHttpTagger(); }
    }
}
```

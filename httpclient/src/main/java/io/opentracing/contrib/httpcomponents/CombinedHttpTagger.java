package io.opentracing.contrib.httpcomponents;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class CombinedHttpTagger implements HttpTagger {

    public static HttpTaggerFactory factory(Iterable<HttpTaggerFactory> taggerFactories) {
        Collection<HttpTaggerFactory> taggerFactories1 = new ArrayList<>();
        for (HttpTaggerFactory taggerFactory : taggerFactories) {
            taggerFactories1.add(taggerFactory);
        }
        return  (span, httpContext) -> {
            final Collection<HttpTagger> taggers = new ArrayList<>(taggerFactories1.size());
            for (HttpTaggerFactory factory : taggerFactories1) {
                taggers.add(factory.create(span, httpContext));
            }
            return new CombinedHttpTagger(taggers);
        };
    }

    private final Iterable<HttpTagger> taggers;

    public CombinedHttpTagger(Iterable<HttpTagger> taggers) {
        this.taggers = taggers;
    }


    public void tagTarget(HttpHost route) {
        for (HttpTagger tagger : taggers) {
            tagger.tagTarget(route);
        }
    }

    public void tagRequest(HttpRequest request) {

    }

    public void tagResponse(HttpResponse context) {

    }
}
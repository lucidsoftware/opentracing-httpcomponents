package io.opentracing.contrib.httpcomponents;

import io.opentracing.Span;

@FunctionalInterface
interface SpanModifier {
    public void modify(Span span);
}

class NoOpSpanModifier implements SpanModifier {
    public static SpanModifier INSTANCE = new NoOpSpanModifier();
    public void modify(Span span) {}
}

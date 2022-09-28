package com.igor101.httpserver;

public interface HttpRequestHandler {
    HttpResponse handle(HttpRequest request);
}

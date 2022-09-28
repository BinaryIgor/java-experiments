package com.igor101.httpserver;

public interface HttpServer {

    void start(HttpRequestHandler handler);

    void stop();
}

package com.igor101.httpserver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
/*

const requests = [];
for (let i =0; i< 1000; i++){
    requests.push(fetch("http:localhost:8080"));
}

console.log(`Performing ${requests.length} requests...`);

Promise.all(requests)
    .then(r => console.log(`Finished ${requests.length} requests!`));

 */

public class HttpServerApp {

    public static void main(String[] args) {
        var server = new SimpleHttpServer(null, 8080, 30_000);

        server.start(r -> {
            var body = """
                    {
                        "id": 1,
                        "url": "%s"
                    }
                    """.formatted(r.url())
                    .getBytes(StandardCharsets.UTF_8);

            var headers = Map.of("Content-Type", List.of("application/json"),
                    "Content-Length", List.of(String.valueOf(body.length)));

            return new HttpResponse(200, headers, body);
        });
    }

}

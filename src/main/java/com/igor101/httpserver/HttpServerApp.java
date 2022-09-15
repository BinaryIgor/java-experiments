package com.igor101.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpServerApp {

    private static final String HTTP_NEW_LINE_SEPARATOR = "\r\n";
    private static final String HTTP_HEAD_BODY_SEPARATOR = HTTP_NEW_LINE_SEPARATOR + HTTP_NEW_LINE_SEPARATOR;
    private static final int HTTP_HEAD_BODY_SEPARATOR_BYTES =
            HTTP_HEAD_BODY_SEPARATOR.getBytes(StandardCharsets.US_ASCII).length;
    private static final int DEFAULT_PACKET_SIZE = 10_000;
    private static final String CONTENT_LENGTH_HEADER = "content-length";

    public static void main(String[] args) throws Exception {
        var serverSocket = new ServerSocket(8080);

        while (true) {
            var connection = serverSocket.accept();

            var requestOpt = readRequest(connection);
            if (requestOpt.isEmpty()) {
                continue;
            }
            printRequest(requestOpt.get());

            try (var os = connection.getOutputStream()) {
                var body = """
                        {
                            "id": 1
                        }
                        """;

                var response = """
                        HTTP/1.1 200 OK
                        Content-Type: application/json
                        Content-Length: %d
                                                
                        %s
                        """.formatted(body.getBytes(StandardCharsets.UTF_8).length, body);

                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static Optional<HttpReq> readRequest(Socket connection) throws Exception {
        var stream = connection.getInputStream();
        var rawRequestHead = readRawRequestHead(stream);

        if (rawRequestHead.length == 0) {
            return Optional.empty();
        }

        var requestHead = new String(rawRequestHead, StandardCharsets.US_ASCII);
        var lines = requestHead.split(HTTP_NEW_LINE_SEPARATOR);

        var line = lines[0];
        var methodUrl = line.split(" ");
        var method = methodUrl[0];
        var url = methodUrl[1];

        var headers = readHeaders(lines);

        var bodyLength = getExpectedBodyLength(headers);

        byte[] body;
        if (bodyLength > 0) {
            var bodyStartIndex = requestHead.indexOf(HTTP_HEAD_BODY_SEPARATOR);
            if (bodyStartIndex > 0) {
                var readBody = Arrays.copyOfRange(rawRequestHead,
                        bodyStartIndex + HTTP_HEAD_BODY_SEPARATOR_BYTES,
                        rawRequestHead.length);
                body = readBody(stream, readBody, bodyLength);
            } else {
                body = new byte[0];
            }
        } else {
            body = new byte[0];
        }

        return Optional.of(new HttpReq(method, url, headers, body));
    }

    private static int getExpectedBodyLength(Map<String, List<String>> headers) {
        try {
            return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH_HEADER, List.of("0")).get(0));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static byte[] readRawRequestHead(InputStream stream) throws Exception {
        var toRead = stream.available();
        if (toRead == 0) {
            toRead = DEFAULT_PACKET_SIZE;
        }

        var buffer = new byte[toRead];
        var read = stream.read(buffer);
        if (read <= 0) {
            return new byte[0];
        }

        return read == toRead ? buffer : Arrays.copyOf(buffer, read);
    }

    private static Map<String, List<String>> readHeaders(String[] lines) {
        var headers = new HashMap<String, List<String>>();

        for (int i = 1; i < lines.length; i++) {
            var line = lines[i];
            if (line.isEmpty()) {
                break;
            }

            var keyValue = line.split(":", 2);
            var key = keyValue[0].toLowerCase().strip();
            var value = keyValue[1].strip();

            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        return headers;
    }

    private static byte[] readBody(InputStream stream, byte[] readBody, int expectedBodyLength) throws Exception {
        if (readBody.length == expectedBodyLength) {
            return readBody;
        }

        var result = new ByteArrayOutputStream(expectedBodyLength);
        result.write(readBody);

        var readBytes = readBody.length;
        var buffer = new byte[DEFAULT_PACKET_SIZE];

        while (readBytes < expectedBodyLength) {
            var read = stream.read(buffer);
            if (read > 0) {
                result.write(buffer, 0, read);
                readBytes += read;
            } else {
                break;
            }
        }

        return result.toByteArray();
    }

    private static void printRequest(HttpReq req) {
        System.out.println("Method: " + req.method);
        System.out.println("Url: " + req.url);
        System.out.println("Headers:");
        req.headers.forEach((k, v) -> {
            System.out.println("%s - %s".formatted(k, v));
        });
        System.out.println("Body:");
        if (req.body.length > 0) {
            System.out.println(new String(req.body, StandardCharsets.UTF_8));
        } else {
            System.out.println("Body is empty");
        }
    }

    private record HttpReq(String method,
                           String url,
                           Map<String, List<String>> headers,
                           byte[] body) {

    }
}

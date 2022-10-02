package com.igor101.tdd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class UrlPathParserTest {

    @Test
    void queryParams_givenAbsoluteUrl_shouldReturnItsQueryParams() {
        var url = "https://google.com?a=1&a=12&b=33";

        var expected = Map.of("a", List.of("1", "12"),
                "b", List.of("33"));
        var actual = UrlPathParser.queryParams(url);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void queryParams_givenUrlPath_shouldReturnItsQueryParams() {
        var path = "/?a=123&b=b-value";

        var expected = Map.of("a", List.of("123"),
                "b", List.of("b-value"));
        var actual = UrlPathParser.queryParams(path);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void queryParams_givenAbsoluteUrlWithoutQueryParams_shouldReturnEmptyMap() {
        var url = "https://example.com";

        var expected = Map.of();
        var actual = UrlPathParser.queryParams(url);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void queryParams_givenUrlPathWithoutQueryParams_shouldReturnEmptyMap() {
        var url = "/path";

        var expected = Map.of();
        var actual = UrlPathParser.queryParams(url);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void pathParams_givenAbsoluteUrlAndTemplate_shouldReturnItsPathVariables() {
        var url = "https://google.com/users/1";
        var template = "/users/:id";

        var expected = Map.of("id", "1");
        var actual = UrlPathParser.pathVariables(url, template);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void pathParams_givenPathAndTemplate_shouldReturnItsPathVariables() {
        var path = "/orders/12/list/87-add-ab-12";
        var template = "/orders/:orderId/list/:listId";

        var expected = Map.of("orderId", "12",
                "listId", "87-add-ab-12");
        var actual = UrlPathParser.pathVariables(path, template);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void pathParams_givenAbsoluteUrlAndNonCompatibleTemplate_shouldThrowException() {
        var url = "https://google.com/users/12";
        var template = "/users/list/:id";

        var exception = Assertions.assertThrows(RuntimeException.class,
                () -> UrlPathParser.pathVariables(url, template));

        Assertions.assertEquals(exception.getMessage(),
                "Invalid variables template. Path has only 3 parts, but more are expected from the template");
    }

    @Test
    void pathParams_givenAbsoluteUrlAndEmptyTemplate_shouldReturnEmptyMap() {
        var url = "https://google.com/users/1";
        var template = "/";

        var expected = Map.of();
        var actual = UrlPathParser.pathVariables(url, template);

        Assertions.assertEquals(expected, actual);
    }
}

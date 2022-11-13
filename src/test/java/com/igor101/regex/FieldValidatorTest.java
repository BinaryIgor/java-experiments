package com.igor101.regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldValidatorTest {

    @ParameterizedTest
    @MethodSource("integerTestCases")
    void shouldValidateInteger(String string, boolean valid) {
        Assertions.assertEquals(valid, FieldValidator.isValidInteger(string));
    }

    @ParameterizedTest
    @MethodSource("doubleTestCases")
    void shouldValidateDouble(String string, boolean valid) {
        Assertions.assertEquals(valid, FieldValidator.isValidDouble(string));
    }

    @ParameterizedTest
    @MethodSource("nameTestCases")
    void shouldValidateName(String string, boolean valid) {
        Assertions.assertEquals(valid, FieldValidator.isValidName(string));
    }

    static Stream<Arguments> integerTestCases() {
        return Stream.of(
                Arguments.of("2", true),
                Arguments.of("2456", true),
                Arguments.of("0", true),
                Arguments.of("-2", true),
                Arguments.of("-224", true),
                Arguments.of("", false),
                Arguments.of(null, false),
                Arguments.of("ada90=-+", false),
                Arguments.of("56 adada", false),
                Arguments.of("Ad dx09", false),
                Arguments.of("Ad 877", false)
        );
    }

    static Stream<Arguments> doubleTestCases() {
        return Stream.of(
                Arguments.of("22", true),
                Arguments.of("0", true),
                Arguments.of("-2", true),
                Arguments.of("-224", true),
                Arguments.of("2.2", true),
                Arguments.of("222.298", true),
                Arguments.of("12,2", true),
                Arguments.of("4,898", true),
                Arguments.of("-8.898", true),
                Arguments.of("-823.8", true),
                Arguments.of("122.8", true),
                Arguments.of("", false),
                Arguments.of(" ", false),
                Arguments.of(null, false),
                Arguments.of("aX9X=-@", false),
                Arguments.of("56 adadadd", false),
                Arguments.of("56.9 adadadd", false),
                Arguments.of("Ad dx09", false),
                Arguments.of("Ad 877.88", false),
                Arguments.of("X 8,88", false));
    }

    static Stream<Arguments> nameTestCases() {
        var tooLongName = Stream.generate(() -> "c")
                .limit(21)
                .collect(Collectors.joining());

        return Stream.of(
                Arguments.of("Ada", true),
                Arguments.of("ax123", true),
                Arguments.of("a12", true),
                Arguments.of("z12_-", true),
                Arguments.of("1dada", false),
                Arguments.of("some name", false),
                Arguments.of("some\name233", false),
                Arguments.of("2344", false),
                Arguments.of(" ", false),
                Arguments.of(null, false),
                Arguments.of("so", false),
                Arguments.of(tooLongName, false));
    }
}

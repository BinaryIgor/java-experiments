package com.igor101.saga.simple.controller;

import java.util.Collection;

public record Response<T>(boolean success, T value, Collection<String> exceptions) {
}

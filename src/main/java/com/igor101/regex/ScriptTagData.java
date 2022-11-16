package com.igor101.regex;

import java.util.Map;

public record ScriptTagData(String script,
                            Map<String, String> properties) {
}

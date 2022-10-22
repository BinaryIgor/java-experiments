package com.igor101.fulltextsearch;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleAnalyzer implements Analyzer {

    private static final String WHITE_SPACES_PATTERN = "\\s+";
    private static final String TO_REMOVE_CHARACTERS_PATTERN = "[.,?!\\-)(;]";

    @Override
    public Collection<String> terms(String text) {
        var tokens = text.split(WHITE_SPACES_PATTERN);

        var terms = new ArrayList<String>();

        for (var t : tokens) {
            var normalized = t.replaceAll(TO_REMOVE_CHARACTERS_PATTERN, "").toLowerCase();
            if (!normalized.isEmpty()) {
                terms.add(normalized);
            }
        }

        return terms;
    }
}

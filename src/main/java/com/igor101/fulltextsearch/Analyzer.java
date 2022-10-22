package com.igor101.fulltextsearch;

import java.util.Collection;

public interface Analyzer {
    Collection<String> terms(String text);
}

package com.igor101.fulltextsearch;

import java.util.*;

public class InvertedIndex {

    private final Map<String, Collection<String>> termsDocs = new HashMap<>();
    private final Analyzer analyzer;

    public InvertedIndex(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    //term -> [doc1, doc2]
    public void addDocument(String docId, String document) {
        analyzer.terms(document)
                .forEach(t -> termsDocs.computeIfAbsent(t, k -> new LinkedHashSet<>()).add(docId));
    }

    public Collection<String> documentsOfTerm(String term) {
        return termsDocs.getOrDefault(term, Set.of());
    }

    public Collection<String> terms() {
        return termsDocs.keySet();
    }
}

package com.igor101.fulltextsearch;

import java.nio.charset.StandardCharsets;

public class FullTextSearchApp {

    public static void main(String[] args) {
        var doc1 = docContent("doc1");
        var doc2 = docContent("doc2");
        var doc3 = docContent("doc3");

        var analyzer = new NgramAnalyzer(new SimpleAnalyzer(), 3, 5);

        var invertedIndex = new InvertedIndex(analyzer);

        invertedIndex.addDocument("doc1", doc1);
        invertedIndex.addDocument("doc2", doc2);
        invertedIndex.addDocument("doc3", doc3);

        System.out.println(invertedIndex.documentsOfTerm("loy"));
    }

    private static String docContent(String docId) {
        try (var is = FullTextSearchApp.class.getResourceAsStream("/fulltextsearch/%s.txt".formatted(docId))) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.igor101.fulltextsearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/*
3-gram of science is:
sci, cie, ien, enc, nce
*/
public class NgramAnalyzer implements Analyzer {

    private final Analyzer analyzer;
    private final int minLength;
    private final int maxLength;

    public NgramAnalyzer(Analyzer analyzer, int minLength, int maxLength) {
        this.analyzer = analyzer;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public Collection<String> terms(String text) {
        var terms = analyzer.terms(text);

        var ngrams = terms.stream()
                .flatMap(t -> termNgrams(t).stream())
                .collect(Collectors.toSet());

        var termsWithNgrams = new HashSet<>(terms);
        termsWithNgrams.addAll(ngrams);

        return termsWithNgrams;
    }

    private Collection<String> termNgrams(String term) {
        var ngrams = new LinkedHashSet<String>();

        for (int i = minLength; i <= maxLength; i++) {
            ngrams.addAll(termNgram(term, i));
        }

        return ngrams;
    }

    //given: (complex, 2) returns:
    //co, om, mp, pl, le, ex
    private Collection<String> termNgram(String term, int length) {
        if (term.length() <= length) {
            return List.of(term);
        }

        var ngrams = new LinkedHashSet<String>();

        for (int i = 0; i <= (term.length() - length); i++) {
            var gram = term.substring(i, i + length);
            ngrams.add(gram);
        }

        return ngrams;
    }
}

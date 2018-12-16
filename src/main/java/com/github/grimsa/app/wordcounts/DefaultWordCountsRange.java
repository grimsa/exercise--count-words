package com.github.grimsa.app.wordcounts;

import com.github.grimsa.app.WordCounts.WordCountsRange;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

class DefaultWordCountsRange implements WordCountsRange {
    private final String name;
    private final Map<String, Integer> wordCountsInRange;

    DefaultWordCountsRange(String name, Map<String, Integer> wordCountsInRange) {
        this.name = name;
        this.wordCountsInRange = wordCountsInRange;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int size() {
        return wordCountsInRange.size();
    }

    @Override
    public Stream<Entry<String, Integer>> streamWordsWithCounts() {
        return Collections.unmodifiableMap(wordCountsInRange).entrySet().stream();
    }
}

package com.github.grimsa.app;

import java.util.Map.Entry;
import java.util.stream.Stream;

public interface WordCounts {
    void add(String word);

    int size();

    int countOf(String word);

    WordCountsRange range(char fromInclusive, char toInclusive);

    interface WordCountsRange {
        String name();

        int size();

        Stream<Entry<String, Integer>> streamWordsWithCounts();
    }
}

package com.github.grimsa.app.wordcounts;

import org.junit.jupiter.api.Test;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentSortedWordCountsTest {
    private final ConcurrentSortedWordCounts wordCounts = new ConcurrentSortedWordCounts();

    @Test
    void initialState_always_empty() {
        assertThat(wordCounts.size()).isEqualTo(0);
    }

    @Test
    void add_sameWordInVariousCases_countedAsSameWord() {
        wordCounts.add("word");
        wordCounts.add("WORD");
        wordCounts.add("Word");

        assertThat(wordCounts.size()).isEqualTo(1);
        assertThat(wordCounts.countOf("word")).isEqualTo(3);
        assertThat(wordCounts.countOf("WORD")).isEqualTo(3);
    }

    @Test
    void add_sameWordInVariousCases_lowercaseValueStored() {
        wordCounts.add("WORD");
        wordCounts.add("Word");

        assertThat(wordCounts.size()).isEqualTo(1);
        var range = wordCounts.range('w', 'w');
        assertThat(range.size()).isEqualTo(1);
        var entry = range.streamWordsWithCounts().collect(Collectors.toList()).get(0);
        assertThat(entry.getKey()).isEqualTo("word");
        assertThat(entry.getValue()).isEqualTo(2);
    }

    @Test
    void add_differentWords_countedAsDifferentWords() {
        wordCounts.add("one");
        wordCounts.add("two");
        wordCounts.add("three");

        assertThat(wordCounts.size()).isEqualTo(3);
        assertThat(wordCounts.countOf("one")).isEqualTo(1);
        assertThat(wordCounts.countOf("two")).isEqualTo(1);
        assertThat(wordCounts.countOf("three")).isEqualTo(1);
    }

    @Test
    void range_noValuesInRange_emptyRange() {
        wordCounts.add("aa");
        wordCounts.add("bb");
        wordCounts.add("cc");
        wordCounts.add("dd");
        wordCounts.add("mm");
        wordCounts.add("xx");
        wordCounts.add("zz");
        wordCounts.add("zzzzzzzzzz");

        var range = wordCounts.range('E', 'F');

        assertThat(range.size()).isEqualTo(0);
        assertThat(range.streamWordsWithCounts()).isEmpty();
    }

    @Test
    void range_rangeInTheMiddle_bothFromAndToAreInclusive() {
        wordCounts.add("aa");
        wordCounts.add("bb");
        wordCounts.add("cc");
        wordCounts.add("dd");
        wordCounts.add("mm");
        wordCounts.add("xx");
        wordCounts.add("zz");
        wordCounts.add("zzzzzzzzzz");

        var range = wordCounts.range('C', 'X');

        assertThat(range.size()).isEqualTo(4);
        assertThat(range.streamWordsWithCounts().map(Entry::getKey)).containsOnly("cc", "dd", "mm", "xx");
    }

    @Test
    void range_rangeIncludesLastLetter_lastEntryIncluded() {
        wordCounts.add("aa");
        wordCounts.add("bb");
        wordCounts.add("cc");
        wordCounts.add("dd");
        wordCounts.add("mm");
        wordCounts.add("xx");
        wordCounts.add("zz");
        wordCounts.add("zzzzzzzzzz");

        var range = wordCounts.range('x', 'z');

        assertThat(range.size()).isEqualTo(3);
        assertThat(range.streamWordsWithCounts().map(Entry::getKey)).containsOnly("xx", "zz", "zzzzzzzzzz");
    }

    @Test
    void range_variousRanges_rangeNameIsInUpperCase() {
        assertThat(wordCounts.range('E', 'F').name()).isEqualTo("E-F");
        assertThat(wordCounts.range('a', 'z').name()).isEqualTo("A-Z");
    }
}
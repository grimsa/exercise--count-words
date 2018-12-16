package com.github.grimsa.app.wordcounts;

import com.github.grimsa.app.WordCounts;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSortedWordCounts implements WordCounts {
    private final ConcurrentNavigableMap<String, Integer> countsByWord = new ConcurrentSkipListMap<>(new CollatingComparator());

    @Override
    public void add(String word) {
        var wordInLowerCase = word.toLowerCase(Locale.ROOT);
        countsByWord.merge(wordInLowerCase, 1, Integer::sum);
    }

    @Override
    public int size() {
        return countsByWord.size();
    }

    @Override
    public int countOf(String word) {
        return countsByWord.getOrDefault(word, 0);
    }

    @Override
    public WordCountsRange range(char fromInclusive, char toInclusive) {
        return new DefaultWordCountsRange(
                rangeName(fromInclusive, toInclusive),
                countsByWordInRange(fromInclusive, toInclusive)
        );
    }

    private String rangeName(char fromInclusive, char toInclusive) {
        return (fromInclusive + "-" + toInclusive).toUpperCase(Locale.ROOT);
    }

    private Map<String, Integer> countsByWordInRange(char fromLetterInclusive, char toLetterInclusive) {
        if (isLastLetter(toLetterInclusive)) {
            return countsByWord.tailMap(key(fromLetterInclusive));
        } else {
            return countsByWord.subMap(key(fromLetterInclusive), key(nextLetter(toLetterInclusive)));
        }
    }

    private boolean isLastLetter(char letter) {
        return Character.toUpperCase(letter) == 'Z';
    }

    private String key(char fromInclusive) {
        return Character.toString(fromInclusive);
    }

    private char nextLetter(char letter) {
        return (char) (letter + 1);
    }

    private static class CollatingComparator implements Comparator<String> {
        private final Collator collator;
        private final ConcurrentMap<String, CollationKey> collationKeys = new ConcurrentHashMap<>();

        CollatingComparator() {
            collator = Collator.getInstance(Locale.ENGLISH);
            collator.setStrength(Collator.PRIMARY);
        }

        @Override
        public int compare(String o1, String o2) {
            return Comparator.comparing(this::collationKey)
                    .compare(o1, o2);
        }

        private CollationKey collationKey(final String string) {
            return collationKeys.computeIfAbsent(string, collator::getCollationKey);
        }
    }
}

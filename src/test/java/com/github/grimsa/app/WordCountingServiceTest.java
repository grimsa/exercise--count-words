package com.github.grimsa.app;

import com.github.grimsa.app.wordcounts.ConcurrentSortedWordCounts;
import com.github.grimsa.generic.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WordCountingServiceTest {
    private final FileService fileService = mock(FileService.class);
    private final WordCountingService wordCountingService = new WordCountingService(fileService);
    @SuppressWarnings("unchecked")
    private final ArgumentCaptor<Stream<String>> lineStreamCaptor = ArgumentCaptor.forClass(Stream.class);

    @Test
    void countWords_always_wordsCounted() {
        var path1 = Path.of("input-1.txt");
        var path2 = Path.of("input-2.txt");
        var inputFilePaths = List.of(path1, path2);
        doAnswer(invocation -> {
            Consumer<String> addToWordCount = invocation.getArgument(1);
            List.of("aha", "moon", "AHA", "Aha", "zoo").forEach(addToWordCount);
            return null;
        }).when(fileService).processAllWords(eq(path1), any());
        doAnswer(invocation -> {
            Consumer<String> addToWordCount = invocation.getArgument(1);
            List.of("zoo", "x-ray", "snake").forEach(addToWordCount);
            return null;
        }).when(fileService).processAllWords(eq(path2), any());

        var wordCounts = wordCountingService.countWords(inputFilePaths);
        assertThat(wordCounts.size()).isEqualTo(5);
        assertThat(wordCounts.countOf("aha")).isEqualTo(3);
        assertThat(wordCounts.countOf("moon")).isEqualTo(1);
        assertThat(wordCounts.countOf("snake")).isEqualTo(1);
        assertThat(wordCounts.countOf("x-ray")).isEqualTo(1);
        assertThat(wordCounts.countOf("zoo")).isEqualTo(2);
    }

    @Test
    void outputRanges_always_fourRangesWrittenToFile() throws IOException {
        var wordCounts = new ConcurrentSortedWordCounts();
        Stream.of(
                "aha", "aha",
                "bad", "big",
                "game",
                "moon",
                "name",
                "one",
                "snake",
                "up",
                "view",
                "x-ray",
                "zoo", "zoo"
        ).forEach(wordCounts::add);

        wordCountingService.outputRanges(wordCounts, Path.of("output-dir"));

        verify(fileService).write(eq(Path.of("output-dir/A-G.txt")), lineStreamCaptor.capture());
        verify(fileService).write(eq(Path.of("output-dir/H-N.txt")), lineStreamCaptor.capture());
        verify(fileService).write(eq(Path.of("output-dir/O-U.txt")), lineStreamCaptor.capture());
        verify(fileService).write(eq(Path.of("output-dir/V-Z.txt")), lineStreamCaptor.capture());
        assertThat(lineStreamCaptor.getAllValues().get(0)).containsOnly("aha 2", "bad 1", "big 1", "game 1");
        assertThat(lineStreamCaptor.getAllValues().get(1)).containsOnly("moon 1", "name 1");
        assertThat(lineStreamCaptor.getAllValues().get(2)).containsOnly("one 1", "snake 1", "up 1");
        assertThat(lineStreamCaptor.getAllValues().get(3)).containsOnly("view 1", "x-ray 1", "zoo 2");
    }
}
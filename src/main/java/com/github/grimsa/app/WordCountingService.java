package com.github.grimsa.app;

import com.github.grimsa.app.WordCounts.WordCountsRange;
import com.github.grimsa.app.wordcounts.ConcurrentSortedWordCounts;
import com.github.grimsa.generic.FileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.github.grimsa.generic.Functions.uncheckedConsumer;

@Service
class WordCountingService {
    private final FileService fileService;

    WordCountingService(FileService fileService) {
        this.fileService = fileService;
    }

    WordCounts countWords(List<Path> inputFiles) {
        var wordCounts = new ConcurrentSortedWordCounts();
        inputFiles.parallelStream()
                .forEach(path -> fileService.processAllWords(path, wordCounts::add));
        return wordCounts;
    }

    void outputRanges(WordCounts wordCounts, Path outputDir) {
        Stream.of(
                wordCounts.range('A', 'G'),
                wordCounts.range('H', 'N'),
                wordCounts.range('O', 'U'),
                wordCounts.range('V', 'Z')
        ).parallel()
                .forEach(uncheckedConsumer(range -> writeToFile(range, outputDir)));
    }

    private void writeToFile(WordCountsRange range, final Path outputDir) throws IOException {
        final Path outputFilePath = outputDir.resolve(range.name() + ".txt");
        var lines = range.streamWordsWithCounts()
                .map(entry -> entry.getKey() + ' ' + entry.getValue());
        fileService.write(outputFilePath, lines);
    }
}

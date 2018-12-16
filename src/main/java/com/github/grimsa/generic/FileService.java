package com.github.grimsa.generic;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class FileService {
    public void write(final Path outputFile, final Stream<String> lines) throws IOException {
        var iterator = lines
                .map(CharSequence.class::cast)
                .iterator();
        Files.write(outputFile, () -> iterator);
    }

    public void processAllWords(final Path inputFile, Consumer<String> wordConsumer) {
        processAllLines(inputFile, line ->
                Arrays.stream(line.split("\\s"))
                        .filter(word -> !word.isEmpty())
                        .forEach(wordConsumer)
        );
    }

    private void processAllLines(final Path inputFile, Consumer<String> lineConsumer) {
        try (Stream<String> lineStream = Files.lines(inputFile)) {
            lineStream.forEach(lineConsumer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

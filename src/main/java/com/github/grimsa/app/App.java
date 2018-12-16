package com.github.grimsa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.grimsa.generic.Functions.uncheckedFunction;

@SpringBootApplication
@ComponentScan({
        "com.github.grimsa.generic",
        "com.github.grimsa.app"
})
public class App implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static final String INPUT_FILE_LOCATION_PATTERN_ARG_NAME = "input-pattern";
    private static final String OUTPUT_DIR_ARG_NAME = "output-dir";
    private final ApplicationContext applicationContext;
    private final WordCountingService wordCountingService;

    public App(ApplicationContext applicationContext, WordCountingService wordCountingService) {
        this.applicationContext = applicationContext;
        this.wordCountingService = wordCountingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        var outputDir = getSingleMandatoryArgValue(args, OUTPUT_DIR_ARG_NAME);
        var inputLocationPattern = getSingleMandatoryArgValue(args, INPUT_FILE_LOCATION_PATTERN_ARG_NAME);
        var inputFiles = getInputFilePaths(inputLocationPattern);

        LOG.info("Counting words in files: " + inputFiles);

        var wordCounts = wordCountingService.countWords(inputFiles);
        wordCountingService.outputRanges(wordCounts, Path.of(outputDir));

        LOG.info("Word counts written to " + outputDir);
    }

    private String getSingleMandatoryArgValue(ApplicationArguments args, String argName) {
        return Optional.ofNullable(args.getOptionValues(argName))
                .filter(values -> values.size() == 1)
                .map(values -> values.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Please provide exactly one value for '" + argName + "' argument."));
    }

    private List<Path> getInputFilePaths(String inputLocationPattern) throws IOException {
        return Stream.of(applicationContext.getResources(inputLocationPattern))
                .map(uncheckedFunction(resource -> resource.getFile().toPath()))
                .collect(Collectors.toList());
    }
}

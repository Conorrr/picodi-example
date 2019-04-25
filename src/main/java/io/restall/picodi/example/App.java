package io.restall.picodi.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restall.picodi.Picodi;
import io.restall.picodi.example.maven.MavenService;
import io.restall.picodi.example.maven.SearchResponseBody;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Example application for picodi that searches maven for dependencies
 */
@CommandLine.Command(name = "search", mixinStandardHelpOptions = true, version = "Picodi example")
public class App implements Runnable {

    private final MavenService mavenService;
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Verbose mode. Helpful for troubleshooting. " +
            "Multiple -v options increase the verbosity.")
    private boolean[] verbose = new boolean[0];

    @CommandLine.Parameters(index = "query", arity = "1..*", description = "maven search query")
    private String[] queryParts;

    public App(MavenService mavenService) {
        this.mavenService = mavenService;
    }

    public void run() {
        String joinedQuery = String.join(" ", queryParts);
        try {
            SearchResponseBody searchResponse = mavenService.search(joinedQuery);
            int resultsFound = searchResponse.getResponse().getNumFound();

            System.out.println(String.format("%s results found", resultsFound));
            System.out.println(String.format("first %s results", Math.min(10, resultsFound)));
            System.out.println(String.format("%-30s %-30s %20s", "group", "artifact", "latest version"));

            searchResponse.getResponse().getDocs().stream()
                    .map(doc -> String.format("%-30s %-30s %20s", doc.getGroup(), doc.getArtifact(), doc.getLatestVersion()))
                    .forEach(System.out::println);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    private static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public static void main(String[] args) {
        CommandLine.IFactory iFactory = new Picodi()
                .register(HttpClient.class, picodi -> createHttpClient())
                .register(ObjectMapper.class, picodi -> createObjectMapper())
                .register(MavenService.class)
                .register(App.class)
                .createIFactory();
        CommandLine.run(App.class, iFactory, args);
    }
}
